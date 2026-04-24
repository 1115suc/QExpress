package course.QExpress.work.mq;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import course.QExpress.common.constant.Constants;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.vo.CourierMsg;
import course.QExpress.common.vo.CourierTaskMsg;
import course.QExpress.oms.api.OrderFeign;
import course.QExpress.oms.domain.dto.OrderDTO;
import course.QExpress.oms.domain.enums.OrderStatus;
import course.QExpress.work.domain.enums.pickupDispatchtask.PickupDispatchTaskAssignedStatus;
import course.QExpress.work.domain.enums.pickupDispatchtask.PickupDispatchTaskSignStatus;
import course.QExpress.work.domain.enums.pickupDispatchtask.PickupDispatchTaskStatus;
import course.QExpress.work.domain.enums.pickupDispatchtask.PickupDispatchTaskType;
import course.QExpress.work.entity.PickupDispatchTaskEntity;
import course.QExpress.work.service.PickupDispatchTaskService;
import course.QExpress.work.service.TransportOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 快递员的消息处理，该处理器处理两个消息：
 * 1. 生成快递员取派件任务
 * 2. 快递员取件成功，订单转运单
 */
@Component
@Slf4j
public class CourierMQListener {

    @Resource
    PickupDispatchTaskService pickupDispatchTaskService;

    @Resource
    TransportOrderService transportOrderService;

    @Resource
    OrderFeign orderFeign;

    /**
     * 生成快递员取派件任务
     *
     * @param msg 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Constants.MQ.Queues.WORK_PICKUP_DISPATCH_TASK_CREATE),
            exchange = @Exchange(name = Constants.MQ.Exchanges.PICKUP_DISPATCH_TASK_DELAYED, type = ExchangeTypes.TOPIC, delayed = Constants.MQ.DELAYED),
            key = Constants.MQ.RoutingKeys.PICKUP_DISPATCH_TASK_CREATE
    ))
    public void listenCourierTaskMsg(String msg) {
        //{"taskType":1,"orderId":225125208064,"created":1654767899885,"courierId":1001,"agencyId":8001,"estimatedStartTime":1654224658728,"mark":"带包装"}
        log.info("接收到快递员任务的消息 >>> msg = {}", msg);
        //解析消息
        CourierTaskMsg courierTaskMsg = JSONUtil.toBean(msg, CourierTaskMsg.class);
        //幂等性处理：判断订单对应的取派件任务是否存在，判断条件：订单号+任务状态
        List<PickupDispatchTaskEntity> list = this.pickupDispatchTaskService.findByOrderId(courierTaskMsg.getOrderId());
        for (PickupDispatchTaskEntity pickupDispatchTaskEntity : list) {
            if (pickupDispatchTaskEntity.getStatus() == PickupDispatchTaskStatus.NEW) {
                //消息重复消费
                return;
            }
        }
        // 订单不存在 不进行调度
        OrderDTO orderDTO = orderFeign.findById(courierTaskMsg.getOrderId());
        if (ObjectUtil.isEmpty(orderDTO)) {
            return;
        }
        // 如果已经取消或者删除 则不进行调度
        if (orderDTO.getStatus().equals(OrderStatus.CANCELLED.getCode()) || orderDTO.getStatus().equals(OrderStatus.DEL.getCode())) {
            return;
        }
        PickupDispatchTaskEntity pickupDispatchTask = BeanUtil.toBean(courierTaskMsg, PickupDispatchTaskEntity.class);
        //任务类型
        pickupDispatchTask.setTaskType(PickupDispatchTaskType.codeOf(courierTaskMsg.getTaskType()));
        //预计开始时间，结束时间向前推一小时
        LocalDateTime estimatedStartTime = LocalDateTimeUtil.offset(pickupDispatchTask.getEstimatedEndTime(), -1, ChronoUnit.HOURS);
        pickupDispatchTask.setEstimatedStartTime(estimatedStartTime);
        // 默认未签收状态
        pickupDispatchTask.setSignStatus(PickupDispatchTaskSignStatus.NOT_SIGNED);
        //分配状态
        if (ObjectUtil.isNotEmpty(pickupDispatchTask.getCourierId())) {
            pickupDispatchTask.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED);
        } else {
            pickupDispatchTask.setAssignedStatus(PickupDispatchTaskAssignedStatus.MANUAL_DISTRIBUTED);
        }
        PickupDispatchTaskEntity result = this.pickupDispatchTaskService.saveTaskPickupDispatch(pickupDispatchTask);
        if (result == null) {
            //保存任务失败
            throw new QEException(StrUtil.format("快递员任务保存失败 >>> msg = {}", msg));
        }
    }

    /**
     * 快递员取件成功
     * @param msg 消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Constants.MQ.Queues.WORK_COURIER_PICKUP_SUCCESS),
            exchange = @Exchange(name = Constants.MQ.Exchanges.COURIER, type = ExchangeTypes.TOPIC),
            key = Constants.MQ.RoutingKeys.COURIER_PICKUP
    ))
    public void listenCourierPickupMsg(String msg) {
        log.info("接收到快递员取件成功的消息 >>> msg = {}", msg);
        //解析消息
        CourierMsg courierMsg = JSONUtil.toBean(msg, CourierMsg.class);
        // 消费消息 调用transportOrderService的订单转运单方法
        this.transportOrderService.orderToTransportOrder(courierMsg.getOrderId());
        //TODO day10 发送运单跟踪消息
    }
}
