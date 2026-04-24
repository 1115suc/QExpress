package course.QExpress.ms.base.service.truck.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.ms.base.entity.truck.TruckEntity;
import course.QExpress.ms.base.entity.truck.TruckLicenseEntity;
import course.QExpress.ms.base.mapper.truck.TruckLicenseMapper;
import course.QExpress.ms.base.service.truck.TruckLicenseService;
import course.QExpress.ms.base.service.truck.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车辆行驶证表  服务类
 */
@Service
@RequiredArgsConstructor
public class TruckLicenseServiceImpl extends ServiceImpl<TruckLicenseMapper, TruckLicenseEntity> implements TruckLicenseService {

    private final TruckService truckService;

    /**
     * 保存车辆行驶证信息
     *
     * @param truckLicenseEntity 车辆行驶证信息
     * @return 车辆行驶证信息
     */
    @Transactional
    @Override
    public TruckLicenseEntity saveTruckLicense(TruckLicenseEntity truckLicenseEntity) {
        if (truckLicenseEntity.getId() == null) {
            super.save(truckLicenseEntity);
            // 处理车辆信息中的关联字段
            if (ObjectUtil.isNotEmpty(truckLicenseEntity.getTruckId())) {
                TruckEntity truckEntity = truckService.getById(truckLicenseEntity.getTruckId());
                truckEntity.setTruckLicenseId(truckLicenseEntity.getId());
                truckService.updateById(truckEntity);
            }
        } else {
            super.updateById(truckLicenseEntity);
        }
        return truckLicenseEntity;
    }
}
