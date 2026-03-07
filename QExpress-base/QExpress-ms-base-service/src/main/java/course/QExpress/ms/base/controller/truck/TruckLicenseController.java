package course.QExpress.ms.base.controller.truck;

import course.QExpress.common.util.BeanUtil;
import course.QExpress.ms.base.domain.truck.TruckLicenseDto;
import course.QExpress.ms.base.entity.truck.TruckLicenseEntity;
import course.QExpress.ms.base.service.truck.TruckLicenseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 车辆行驶证业务
 */
@RestController
@RequestMapping("base/truck/license")
public class TruckLicenseController {

    @Resource
    private TruckLicenseService truckLicenseService;

    /**
     * 保存车辆行驶证信息
     *
     * @param dto 车辆行驶证信息
     * @return 车辆行驶证信息
     */
    @PostMapping
    public TruckLicenseDto saveTruckLicense(@RequestBody TruckLicenseDto dto) {
        TruckLicenseEntity pdTruckLicenseEntity = BeanUtil.toBean(dto, TruckLicenseEntity.class);
        pdTruckLicenseEntity = truckLicenseService.saveTruckLicense(pdTruckLicenseEntity);
        BeanUtil.copyProperties(pdTruckLicenseEntity, dto);
        return dto;
    }

    /**
     * 根据id获取车辆行驶证详情
     *
     * @param id 车辆行驶证id
     * @return 车辆行驶证信息
     */
    @GetMapping("/{id}")
    public TruckLicenseDto fineById(@PathVariable(name = "id") Long id) {
        TruckLicenseEntity pdTruckLicense = truckLicenseService.getById(id);
        return BeanUtil.toBean(pdTruckLicense, TruckLicenseDto.class);
    }
}
