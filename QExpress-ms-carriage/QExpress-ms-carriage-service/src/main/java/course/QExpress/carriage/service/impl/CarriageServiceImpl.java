package course.QExpress.carriage.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.carriage.domain.constant.CarriageConstant;
import course.QExpress.carriage.domain.dto.CarriageDTO;
import course.QExpress.carriage.domain.dto.WaybillDTO;
import course.QExpress.carriage.domain.enums.EconomicRegionEnum;
import course.QExpress.carriage.entity.CarriageEntity;
import course.QExpress.carriage.enums.CarriageExceptionEnum;
import course.QExpress.carriage.mapper.CarriageMapper;
import course.QExpress.carriage.service.CarriageService;
import course.QExpress.carriage.utils.CarriageUtils;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.ms.base.api.common.AreaFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName 类名
 * @Description 类说明
 */
@Slf4j
@Service
public class CarriageServiceImpl extends ServiceImpl<CarriageMapper, CarriageEntity> implements CarriageService {
    /**
     * 流程说明：
     * ● 根据传入的CarriageDTO对象参数进行查询模板，判断模板是否存在，如果不存在直接落库
     * ● 如果存在，需要进一步的判断是否为经济区互寄，如果不是，说明模板重复，不能落库
     * ● 如果是经济区互寄，再进一步的判断是否有重复的城市，如果是，模板重复，不能落库
     * ● 如果不重复，落库，响应返回
     * 模板为什么不能重复？
     * 因为运费的计算是通过模板进行的，如果存在多个模板，该基于哪个模板计算呢？所以模板是不能重复的。
     *
     * @param carriageDto 新增/修改运费对象
     * @return 模板类型常量
     */
    @Resource
    private AreaFeign areaFeign;

    @Override
    public CarriageDTO saveOrUpdate(CarriageDTO carriageDto) {
        // TODO day02 保存或修改 运费模板
        // 校验运费模板是否存在，如果不存在直接插入 (查询条件： 模板类型  运输类型   如果是修改排除当前id)
        LambdaQueryWrapper<CarriageEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(CarriageEntity::getTemplateType, carriageDto.getTemplateType())
                .eq(CarriageEntity::getTransportType, carriageDto.getTransportType())
                .ne(ObjectUtil.isNotEmpty(carriageDto.getId()), CarriageEntity::getId, carriageDto.getId());
        // 如果没有重复的模板，可以直接插入或更新操作 (DTO 转 entity 保存成功 entity 转 DTO)
        List<CarriageEntity> carriageList = super.list(queryWrapper);
        if (CollectionUtil.isEmpty(carriageList)) {
            return saveOrUpdateCarriage(carriageDto);
        }
        // 如果存在重复模板，需要判断此次插入的是否为经济区互寄，非经济区互寄是不可以重复的
        if (ObjectUtil.notEqual(carriageDto.getTemplateType(), CarriageConstant.ECONOMIC_ZONE)) {
            throw new QEException(CarriageExceptionEnum.NOT_ECONOMIC_ZONE_REPEAT);
        }
        // 如果是经济区互寄类型，需进一步判断关联城市是否重复，通过集合取交集判断是否重复
        List<String> allList = carriageList.stream().map(CarriageEntity::getAssociatedCity)
                .map(associatedCity -> StrUtil.splitToArray(associatedCity, ','))
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        //取交集
        Collection<String> intersection = CollUtil.intersection(allList, carriageDto.getAssociatedCityList());
        if (CollUtil.isNotEmpty(intersection)) {
            //重复
            throw new QEException(CarriageExceptionEnum.ECONOMIC_ZONE_CITY_REPEAT);
        }
        //没有重复，可以新增或更新
        return this.saveOrUpdateCarriage(carriageDto);
    }

    @NotNull
    private CarriageDTO saveOrUpdateCarriage(CarriageDTO carriageDto) {
        CarriageEntity carriageEntity = CarriageUtils.toEntity(carriageDto);
        super.saveOrUpdate(carriageEntity);
        return CarriageUtils.toDTO(carriageEntity);
    }

    @Override
    public List<CarriageDTO> findAll() {
        // TODO day02 查询运费模板
        // 构造查询条件，按创建时间倒序
        LambdaQueryWrapper<CarriageEntity> queryWrapper = Wrappers.<CarriageEntity>lambdaQuery();
        queryWrapper.orderByDesc(CarriageEntity::getCreated);
        // 查询数据库
        List<CarriageEntity> list = super.list(queryWrapper);
        // 将结果转换为DTO类型  使用CarriageUtils工具类
        return list.stream().map(CarriageUtils::toDTO).collect(Collectors.toList());
    }

    /**
     * ● 运费模板优先级：同城>省内>经济区互寄>跨省
     * ● 将体积转化成重量，与重量比较，取大值
     *
     * @param waybillDTO 运费计算对象
     * @return //TODO day02练习 模板缓存  推荐hash结构   大key自定义   小key:发件城市id_收件城市id value: 模板数据
     */
    @Override
    public CarriageDTO compute(WaybillDTO waybillDTO) {
        //根据参数查找运费模板
        CarriageEntity carriage = this.findCarriage(waybillDTO);
        //计算重量，最小重量为1kg
        double computeWeight = this.getComputeWeight(waybillDTO, carriage);
        //计算运费，首重 + 续重
        double expense = carriage.getFirstWeight() + ((computeWeight - 1) * carriage.getContinuousWeight());
        //保留一位小数
        expense = NumberUtil.round(expense, 1).doubleValue();
        //封装运费和计算重量到DTO，并返回
        CarriageDTO carriageDTO = CarriageUtils.toDTO(carriage);
        carriageDTO.setExpense(expense);
        carriageDTO.setComputeWeight(computeWeight);
        return carriageDTO;
    }

    /**
     * 根据体积参数与实际重量计算计费重量
     *
     * @param waybillDTO 运费计算对象
     * @param carriage   运费模板
     * @return 计费重量
     */
    private double getComputeWeight(WaybillDTO waybillDTO, CarriageEntity carriage) {
        //计算体积，如果传入体积不需要计算
        Integer volume = waybillDTO.getVolume();
        if (ObjectUtil.isEmpty(volume)) {
            try {
                //长*宽*高计算体积
                volume = waybillDTO.getMeasureLong() * waybillDTO.getMeasureWidth() * waybillDTO.getMeasureHigh();
            } catch (Exception e) {
                //计算出错设置体积为0
                volume = 0;
            }
        }
        // 计算体积重量，体积 / 轻抛系数
        BigDecimal volumeWeight = NumberUtil.div(volume, carriage.getLightThrowingCoefficient(), 1);
        //取大值
        double computeWeight = NumberUtil.max(volumeWeight.doubleValue(), NumberUtil.round(waybillDTO.getWeight(), 1).doubleValue());
        //计算续重，规则：不满1kg，按1kg计费；10kg以下续重以0.1kg计量保留1位小数；10-100kg续重以0.5kg计量保留1位小数；100kg以上四舍五入取整
        if (computeWeight <= 1) {
            return 1;
        }
        if (computeWeight <= 10) {
            return computeWeight;
        }
        // 举例：
        // 108.4kg按照108kg收费
        // 108.5kg按照109kg收费
        // 108.6kg按照109kg收费
        if (computeWeight >= 100) {
            return NumberUtil.round(computeWeight, 0).doubleValue();
        }
        //0.5为一个计算单位，举例：
        // 18.8kg按照19收费，
        // 18.4kg按照18.5kg收费
        // 18.1kg按照18.5kg收费
        // 18.6kg按照19收费
        int integer = NumberUtil.round(computeWeight, 0, RoundingMode.DOWN).intValue();

        if (NumberUtil.sub(computeWeight, integer) == 0) {
            return integer;
        }

        if (NumberUtil.sub(computeWeight, integer) <= 0.5) {
            return NumberUtil.add(integer, 0.5);
        }
        return NumberUtil.add(integer, 1);
    }

    /**
     * 根据参数查找运费模板
     * 运费模板优先级：同城>省内>经济区互寄>跨省
     *
     * @param waybillDTO 参数
     * @return 运费模板
     */
    private CarriageEntity findCarriage(WaybillDTO waybillDTO) {
        //运费模板优先级：同城>省内>经济区互寄>跨省
        if (ObjectUtil.equals(waybillDTO.getReceiverCityId(), waybillDTO.getSenderCityId())) {
            //同城
            CarriageEntity carriage = this.findByTemplateType(CarriageConstant.SAME_CITY);
            if (ObjectUtil.isNotEmpty(carriage)) {
                return carriage;
            }
        }
        // 获取收寄件地址省份id
        Long receiverProvinceId = this.areaFeign.get(waybillDTO.getReceiverCityId()).getParentId();
        Long senderProvinceId = this.areaFeign.get(waybillDTO.getSenderCityId()).getParentId();
        if (ObjectUtil.equal(receiverProvinceId, senderProvinceId)) {
            //省内
            CarriageEntity carriage = this.findByTemplateType(CarriageConstant.SAME_PROVINCE);
            if (ObjectUtil.isNotEmpty(carriage)) {
                return carriage;
            }
        }
        //经济区互寄
        CarriageEntity carriage = this.findEconomicCarriage(receiverProvinceId, senderProvinceId);
        if (ObjectUtil.isNotEmpty(carriage)) {
            return carriage;
        }
        //跨省
        carriage = this.findByTemplateType(CarriageConstant.TRANS_PROVINCE);
        if (ObjectUtil.isNotEmpty(carriage)) {
            return carriage;
        }
        throw new QEException(CarriageExceptionEnum.NOT_FOUND);
    }

    /**
     * @param receiverProvinceId 收件省份id
     * @param senderProvinceId   发件省份id
     * @return
     */
    private CarriageEntity findEconomicCarriage(Long receiverProvinceId, Long senderProvinceId) {
        //通过工具类EnumUtil 获取经济区城市配置枚举
        LinkedHashMap<String, EconomicRegionEnum> EconomicRegionMap = EnumUtil.getEnumMap(EconomicRegionEnum.class);
        // 遍历所有经济区枚举值
        EconomicRegionEnum economicRegionEnum = null;
        for (EconomicRegionEnum regionEnum : EconomicRegionMap.values()) {
            //该经济区是否全部包含收发件省id
            boolean result = ArrayUtil.containsAll(regionEnum.getValue(), receiverProvinceId, senderProvinceId);
            if (result) {
                economicRegionEnum = regionEnum;
                break;
            }
        }

        if (null == economicRegionEnum) {
            return null;
        }

        //根据类型编码查询
        LambdaQueryWrapper<CarriageEntity> queryWrapper = Wrappers.lambdaQuery(CarriageEntity.class)
                .eq(CarriageEntity::getTemplateType, CarriageConstant.ECONOMIC_ZONE)
                .eq(CarriageEntity::getTransportType, CarriageConstant.REGULAR_FAST)
                .like(CarriageEntity::getAssociatedCity, economicRegionEnum.getCode());
        return super.getOne(queryWrapper);
    }

    /**
     * 根据模板类型查询模板
     *
     * @param templateType 模板类型：1-同城寄，2-省内寄，3-经济区互寄，4-跨省
     * @return 运费模板
     */
    private CarriageEntity findByTemplateType(Integer templateType) {
        // 根据模板类型，及运输类型 = CarriageConstant.REGULAR_FAST 查询模板
        LambdaQueryWrapper<CarriageEntity> queryWrapper = Wrappers.lambdaQuery(CarriageEntity.class)
                .eq(CarriageEntity::getTemplateType, templateType)
                .eq(CarriageEntity::getTransportType, CarriageConstant.REGULAR_FAST);
        return super.getOne(queryWrapper);
    }
}
