package course.QExpress.ms.base.service.truck;

import com.baomidou.mybatisplus.extension.service.IService;
import course.QExpress.common.util.PageResponse;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterDTO;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterListDTO;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterPageQueryDTO;
import course.QExpress.ms.base.entity.truck.TruckReturnRegisterEntity;

/**
 * 回车登记 服务类
 */
public interface TruckReturnRegisterService extends IService<TruckReturnRegisterEntity> {
    /**
     * 分页查询回车登记列表
     *
     * @param dto 分页查询条件
     * @return 回车登记分页结果
     */
    PageResponse<TruckReturnRegisterListDTO> pageQuery(TruckReturnRegisterPageQueryDTO dto);

    /**
     * 根据id查询回车登记详情
     *
     * @param id 回车登记id
     * @return 回车登记详情
     */
    TruckReturnRegisterDTO findById(Long id);
}
