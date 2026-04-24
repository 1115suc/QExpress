package course.QExpress.web.manager.service;

import course.QExpress.common.util.PageResponse;
import course.QExpress.web.manager.vo.baseTruck.TruckReturnRegisterListVO;
import course.QExpress.web.manager.vo.baseTruck.TruckReturnRegisterPageQueryVO;
import course.QExpress.web.manager.vo.baseTruck.TruckReturnRegisterVO;

public interface TruckReturnRegisterService {

    /**
     * 分页查询回车登记列表
     *
     * @param vo 回车登记分页查询条件
     * @return 回车登记分页结果
     */
    PageResponse<TruckReturnRegisterListVO> pageQuery(TruckReturnRegisterPageQueryVO vo);

    /**
     * 回车登记详情
     *
     * @param id 回车登记id
     * @return 回车登记详情
     */
    TruckReturnRegisterVO detail(Long id);
}
