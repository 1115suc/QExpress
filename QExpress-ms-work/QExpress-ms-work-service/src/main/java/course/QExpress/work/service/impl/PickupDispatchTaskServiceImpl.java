package course.QExpress.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.PageResponse;
import course.QExpress.common.vo.OrderMsg;
import course.QExpress.oms.api.OrderFeign;
import course.QExpress.oms.domain.enums.OrderStatus;
import course.QExpress.work.domain.dto.CourierTaskCountDTO;
import course.QExpress.work.domain.dto.PickupDispatchTaskDTO;
import course.QExpress.work.domain.dto.request.PickupDispatchTaskPageQueryDTO;
import course.QExpress.work.domain.enums.WorkExceptionEnum;
import course.QExpress.work.domain.enums.pickupDispatchtask.*;
import course.QExpress.work.entity.PickupDispatchTaskEntity;
import course.QExpress.work.mapper.TaskPickupDispatchMapper;
import course.QExpress.work.service.PickupDispatchTaskService;
import course.QExpress.work.service.TransportOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName 类名
 * @Description 类说明
 */
@Slf4j
@Service
public class PickupDispatchTaskServiceImpl extends ServiceImpl<TaskPickupDispatchMapper, PickupDispatchTaskEntity> implements PickupDispatchTaskService {

    @Resource
    private TransportOrderService transportOrderService;

    @Resource
    private OrderFeign orderFeign;

    /**
     * 任务状态: {@link PickupDispatchTaskStatus}
     *
     * 取消原因: {@link PickupDispatchTaskCancelReason}
     *
     * 运单状态: {@link OrderStatus}
     * @param pickupDispatchTaskDTO 修改的数据
     * @return
     */
    @Override
    @Transactional
    public Boolean updateStatus(PickupDispatchTaskDTO pickupDispatchTaskDTO) {
        //TODO day09 修改取派件任务状态
        WorkExceptionEnum paramError = WorkExceptionEnum.PICKUP_DISPATCH_TASK_PARAM_ERROR;
        //1. 校验: id 和 状态不能为空  否则抛异常
        if (ObjectUtil.hasEmpty(pickupDispatchTaskDTO.getId(), pickupDispatchTaskDTO.getStatus())) {
            throw new QEException("更新取派件任务状态，id或status不能为空", paramError.getCode());
        }
        //2. 根据id查询取派件任务
        PickupDispatchTaskEntity pickupDispatchTask = super.getById(pickupDispatchTaskDTO.getId());
        if (ObjectUtil.isEmpty(pickupDispatchTask)) {
            throw new QEException("更新取派件任务状态，取派件任务不存在", paramError.getCode());
        }
        PickupDispatchTaskStatus status = pickupDispatchTask.getStatus();
        //3. 如果任务状态为NEW  抛出异常 (修改状态方法不允许)
        switch ( status) {
            case NEW:
                throw new QEException(WorkExceptionEnum.PICKUP_DISPATCH_TASK_STATUS_NOT_NEW);
            case COMPLETED:
                //4. 如果任务状态为COMPLETED
                //4.1 设置任务状态为已完成
                pickupDispatchTask.setStatus(PickupDispatchTaskStatus.COMPLETED);
                //4.2 设置实际结束时间为当前时间
                pickupDispatchTask.setActualEndTime(LocalDateTime.now());
                //4.3 如果任务状态为派件任务
                if (PickupDispatchTaskType.DISPATCH == pickupDispatchTask.getTaskType()) {
                    //如果是派件任务的完成，已签收需要设置签收状态和签收人，拒收只需要设置签收状态
                    if (ObjectUtil.isEmpty(pickupDispatchTaskDTO.getSignStatus())) {
                        throw new QEException("完成派件任务，签收状态不能为空", paramError.getCode());
                    }
                    //4.3.1 需要设置签收状态 和 签收人
                    pickupDispatchTask.setSignStatus(pickupDispatchTaskDTO.getSignStatus());
                    if (PickupDispatchTaskSignStatus.RECEIVED == pickupDispatchTaskDTO.getSignStatus()) {
                        if (ObjectUtil.isEmpty(pickupDispatchTaskDTO.getSignRecipient())) {
                            throw new QEException("完成派件任务，签收人不能为空", paramError.getCode());
                        }
                        pickupDispatchTask.setSignRecipient(pickupDispatchTaskDTO.getSignRecipient());
                    }
                }
                break;
            case CANCELLED:
                if (ObjectUtil.isEmpty(pickupDispatchTaskDTO.getCancelReason())) {
                    throw new QEException("取消任务，原因不能为空", paramError.getCode());
                }
                //5. 如果任务状态取消CANCELLED
                //5.1 设置状态为取消状态
                pickupDispatchTask.setStatus(PickupDispatchTaskStatus.CANCELLED);
                //5.2 设置取消原因 取消原因描述  取消时间
                pickupDispatchTask.setCancelReason(pickupDispatchTaskDTO.getCancelReason());
                //5.3 如果取消原因 为: 因快递员原因无法取件
                pickupDispatchTask.setCancelReasonDescription(pickupDispatchTaskDTO.getCancelReasonDescription());
                pickupDispatchTask.setCancelTime(LocalDateTime.now());
                //5.3.1  重新发送待取件消息到调度中心 (后续触发重新派件操作)   sendPickupDispatchTaskMsgAgain
                if (pickupDispatchTaskDTO.getCancelReason() == PickupDispatchTaskCancelReason.RETURN_TO_AGENCY) {
                    //发送分配快递员派件任务的消息
                    OrderMsg orderMsg = OrderMsg.builder()
                            .agencyId(pickupDispatchTask.getAgencyId())
                            .orderId(pickupDispatchTask.getOrderId())
                            .created(DateUtil.current())
                            .taskType(PickupDispatchTaskType.PICKUP.getCode()) //取件任务
                            .mark(pickupDispatchTask.getMark())
                            .estimatedEndTime(pickupDispatchTask.getEstimatedEndTime()).build();
                    //发送消息（取消任务发生在取件之前，没有运单，参数直接填入null）
                    this.transportOrderService.sendPickupDispatchTaskMsgToDispatch(null, orderMsg);
                } else if (pickupDispatchTaskDTO.getCancelReason() == PickupDispatchTaskCancelReason.CANCEL_BY_USER) {
                    //5.4 如果取消原因 为: 用户主动取消  远程调用订单服务修改订单状态为取消状态
                    //5.5 如果是其它取消原因  远程调用订单服务修改订单状态为关闭运单状态
                    //原因是用户取消，则订单状态改为取消
                    orderFeign.updateStatus(ListUtil.of(pickupDispatchTask.getOrderId()), OrderStatus.CANCELLED.getCode());
                } else {
                    //其他原因则关闭订单
                    orderFeign.updateStatus(ListUtil.of(pickupDispatchTask.getOrderId()), OrderStatus.CLOSE.getCode());
                }
                break;
            default:
                throw new QEException("其他未知状态，不能完成更新操作", paramError.getCode());
        }
        //6 根据ID修改取快件任务
        return super.updateById(pickupDispatchTask);
    }

    @Override
    public Boolean updateCourierId(Long id, Long originalCourierId, Long targetCourierId) {
        if (ObjectUtil.hasEmpty(id, targetCourierId, originalCourierId)) {
            throw new QEException(WorkExceptionEnum.UPDATE_COURIER_PARAM_ERROR);
        }
        if (ObjectUtil.equal(originalCourierId, targetCourierId)) {
            throw new QEException(WorkExceptionEnum.UPDATE_COURIER_EQUAL_PARAM_ERROR);
        }
        PickupDispatchTaskEntity pickupDispatchTask = super.getById(id);
        if (ObjectUtil.isEmpty(pickupDispatchTask)) {
            throw new QEException(WorkExceptionEnum.PICKUP_DISPATCH_TASK_NOT_FOUND);
        }
        //校验原快递id是否正确（本来无快递员id的情况除外）
        if (ObjectUtil.isNotEmpty(pickupDispatchTask.getCourierId())
                && ObjectUtil.notEqual(pickupDispatchTask.getCourierId(), originalCourierId)) {
            throw new QEException(WorkExceptionEnum.UPDATE_COURIER_ID_PARAM_ERROR);
        }
        //更改快递员id
        pickupDispatchTask.setCourierId(targetCourierId);
        // 标识已分配状态
        pickupDispatchTask.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED);
        //TODO 发送消息，同步更新快递员任务(ES)
        return super.updateById(pickupDispatchTask);
    }

    /**
     * 取派件任务状态枚举: {@link PickupDispatchTaskStatus}
     * @param taskPickupDispatch 取派件任务信息
     * @return
     */
    @Override
    public PickupDispatchTaskEntity saveTaskPickupDispatch(PickupDispatchTaskEntity taskPickupDispatch) {
        // 设置任务状态为新任务
        taskPickupDispatch.setStatus(PickupDispatchTaskStatus.NEW);
        boolean result = super.save(taskPickupDispatch);
        if (result) {
            //TODO 同步快递员任务到es
            //TODO 生成运单跟踪消息和快递员端取件/派件消息通知
            return taskPickupDispatch;
        }
        throw new QEException(WorkExceptionEnum.PICKUP_DISPATCH_TASK_SAVE_ERROR);
    }

    /**
     * 分页查询取派件任务
     * @param dto 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<PickupDispatchTaskDTO> findByPage(PickupDispatchTaskPageQueryDTO dto) {
        //1.构造条件
        Page<PickupDispatchTaskEntity> iPage = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<PickupDispatchTaskEntity> queryWrapper = Wrappers.<PickupDispatchTaskEntity>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(dto.getId()), PickupDispatchTaskEntity::getId, dto.getId())
                .like(ObjectUtil.isNotEmpty(dto.getOrderId()), PickupDispatchTaskEntity::getOrderId, dto.getOrderId())
                .eq(ObjectUtil.isNotEmpty(dto.getAgencyId()), PickupDispatchTaskEntity::getAgencyId, dto.getAgencyId())
                .eq(ObjectUtil.isNotEmpty(dto.getCourierId()), PickupDispatchTaskEntity::getCourierId, dto.getCourierId())
                .eq(ObjectUtil.isNotEmpty(dto.getTaskType()), PickupDispatchTaskEntity::getTaskType, dto.getTaskType())
                .eq(ObjectUtil.isNotEmpty(dto.getStatus()), PickupDispatchTaskEntity::getStatus, dto.getStatus())
                .eq(ObjectUtil.isNotEmpty(dto.getAssignedStatus()), PickupDispatchTaskEntity::getAssignedStatus, dto.getAssignedStatus())
                .eq(ObjectUtil.isNotEmpty(dto.getSignStatus()), PickupDispatchTaskEntity::getSignStatus, dto.getSignStatus())
                .eq(ObjectUtil.isNotEmpty(dto.getIsDeleted()), PickupDispatchTaskEntity::getIsDeleted, dto.getIsDeleted())
                .between(ObjectUtil.isNotEmpty(dto.getMinEstimatedEndTime()), PickupDispatchTaskEntity::getEstimatedEndTime, dto.getMinEstimatedEndTime(), dto.getMaxEstimatedEndTime())
                .between(ObjectUtil.isNotEmpty(dto.getMinActualEndTime()), PickupDispatchTaskEntity::getActualEndTime, dto.getMinActualEndTime(), dto.getMaxActualEndTime())
                .orderByDesc(PickupDispatchTaskEntity::getUpdated);
        //2.分页查询
        Page<PickupDispatchTaskEntity> result = super.page(iPage, queryWrapper);
        if (ObjectUtil.isEmpty(result.getRecords())) {
            return new PageResponse<>(result);
        }
        //3.组装分页数据
        return new PageResponse(result, PickupDispatchTaskDTO.class);
    }

    @Resource
    private TaskPickupDispatchMapper taskPickupDispatchMapper;

    /**
     * 需要在{@link TaskPickupDispatchMapper}写sql
     * @param courierIds             快递员id列表
     * @param pickupDispatchTaskType 任务类型
     * @param date                   日期，格式：yyyy-MM-dd 或 yyyyMMdd
     * @return
     */
    @Override
    public List<CourierTaskCountDTO> findCountByCourierIds(List<Long> courierIds, PickupDispatchTaskType pickupDispatchTaskType, String date) {
        // TODO day09 查询指定快递员的任务数量

        //1. 计算一天的时间的边界  tips: 使用hutool 的 DateUtil
        DateTime dateTime = DateUtil.parse(date);
        LocalDateTime startDateTime = DateUtil.beginOfDay(dateTime).toLocalDateTime();
        LocalDateTime endDateTime = DateUtil.endOfDay(dateTime).toLocalDateTime();

        return this.taskPickupDispatchMapper.findCountByCourierIds(courierIds, pickupDispatchTaskType.getCode(), startDateTime, endDateTime);
    }

    /**
     * 查询指定快递员当天所有的派件取件任务
     * 删除状态 {@link PickupDispatchTaskIsDeleted}
     * @param courierId 快递员id
     * @return
     */
    @Override
    public List<PickupDispatchTaskDTO> findTodayTaskByCourierId(Long courierId) {
        //查询指定快递员当天所有的派件取件任务
        LambdaQueryWrapper<PickupDispatchTaskEntity> queryWrapper = Wrappers.<PickupDispatchTaskEntity>lambdaQuery()
                .eq(PickupDispatchTaskEntity::getCourierId, courierId)
                .ge(PickupDispatchTaskEntity::getEstimatedStartTime, LocalDateTimeUtil.beginOfDay(LocalDateTime.now()))
                .le(PickupDispatchTaskEntity::getEstimatedStartTime, LocalDateTimeUtil.endOfDay(LocalDateTime.now()))
                .eq(PickupDispatchTaskEntity::getIsDeleted, PickupDispatchTaskIsDeleted.NOT_DELETED);
        List<PickupDispatchTaskEntity> list = super.list(queryWrapper);
        return BeanUtil.copyToList(list, PickupDispatchTaskDTO.class);
    }

    /**
     *
     * @param orderId  订单id
     * @param taskType 任务类型
     * @return
     */
    @Override
    public List<PickupDispatchTaskEntity> findByOrderId(Long orderId, PickupDispatchTaskType taskType) {
        LambdaQueryWrapper<PickupDispatchTaskEntity> wrapper = Wrappers.<PickupDispatchTaskEntity>lambdaQuery()
                .eq(PickupDispatchTaskEntity::getOrderId, orderId)
                .eq(PickupDispatchTaskEntity::getTaskType, taskType)
                .orderByAsc(PickupDispatchTaskEntity::getCreated);
        return this.list(wrapper);
    }
    @Override
    public List<PickupDispatchTaskEntity> findByOrderId(Long orderId) {
        LambdaQueryWrapper<PickupDispatchTaskEntity> wrapper = Wrappers.<PickupDispatchTaskEntity>lambdaQuery()
                .eq(PickupDispatchTaskEntity::getOrderId, orderId)
                .orderByAsc(PickupDispatchTaskEntity::getCreated);
        return this.list(wrapper);
    }

    /**
     * 删除状态 {@link PickupDispatchTaskIsDeleted}
     * @param ids id列表
     * @return
     */
    @Override
    public boolean deleteByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        // 通过id列表构造对象列表
        List<PickupDispatchTaskEntity> list = ids.stream().map(id -> {
            PickupDispatchTaskEntity dispatchTaskEntity = new PickupDispatchTaskEntity();
            dispatchTaskEntity.setId(id);
            dispatchTaskEntity.setIsDeleted(PickupDispatchTaskIsDeleted.IS_DELETED);
            //TODO 发送消息，同步更新快递员任务（ES）
            return dispatchTaskEntity;
        }).collect(Collectors.toList());
        return super.updateBatchById(list);
    }

    /**
     * 今日任务分类计数
     *
     * @param courierId 快递员id
     * @param taskType  任务类型，1为取件任务，2为派件任务
     * @param status    任务状态,1新任务，2已完成，3已取消
     * @param isDeleted 是否逻辑删除
     * @return 任务数量
     */
    @Override
    public Integer todayTasksCount(Long courierId, PickupDispatchTaskType taskType, PickupDispatchTaskStatus status, PickupDispatchTaskIsDeleted isDeleted) {
        //构建查询条件
        LambdaQueryWrapper<PickupDispatchTaskEntity> queryWrapper = Wrappers.<PickupDispatchTaskEntity>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(courierId), PickupDispatchTaskEntity::getCourierId, courierId)
                .eq(ObjectUtil.isNotEmpty(taskType), PickupDispatchTaskEntity::getTaskType, taskType)
                .eq(ObjectUtil.isNotEmpty(status), PickupDispatchTaskEntity::getStatus, status)
                .eq(ObjectUtil.isNotEmpty(isDeleted), PickupDispatchTaskEntity::getIsDeleted, isDeleted);
        //根据任务状态限定查询的日期条件
        LocalDateTime startTime = LocalDateTimeUtil.of(DateUtil.beginOfDay(new Date()));
        LocalDateTime endTime = LocalDateTimeUtil.of(DateUtil.endOfDay(new Date()));
        if (status == null) {
            //没有任务状态,查询任务创建时间
            queryWrapper.between(PickupDispatchTaskEntity::getCreated, startTime, endTime);
        } else if (status == PickupDispatchTaskStatus.NEW) {
            //新任务状态，查询预计结束时间
            queryWrapper.between(PickupDispatchTaskEntity::getEstimatedEndTime, startTime, endTime);
        } else if (status == PickupDispatchTaskStatus.COMPLETED) {
            //完成状态，查询实际完成时间
            queryWrapper.between(PickupDispatchTaskEntity::getActualEndTime, startTime, endTime);
        } else if (status == PickupDispatchTaskStatus.CANCELLED) {
            //取消状态，查询取消时间
            queryWrapper.between(PickupDispatchTaskEntity::getCancelTime, startTime, endTime);
        }
        //结果返回integer类型值
        return Convert.toInt(super.count(queryWrapper));
    }

    private void sendPickupDispatchTaskMsgAgain(PickupDispatchTaskEntity pickupDispatchTask){
        OrderMsg orderMsg = OrderMsg.builder()
        .agencyId(pickupDispatchTask.getAgencyId())
        .orderId(pickupDispatchTask.getOrderId())
        .created(DateUtil.current())
        .taskType(PickupDispatchTaskType.PICKUP.getCode()) //取件任务
        .mark(pickupDispatchTask.getMark())
        .estimatedEndTime(pickupDispatchTask.getEstimatedEndTime()).build();
        //发送消息（取消任务发生在取件之前，没有运单，参数直接填入null）
        this.transportOrderService.sendPickupDispatchTaskMsgToDispatch(null, orderMsg);
        }

}
