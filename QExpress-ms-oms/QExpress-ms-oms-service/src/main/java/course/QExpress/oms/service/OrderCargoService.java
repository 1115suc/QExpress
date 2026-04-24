package course.QExpress.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import course.QExpress.oms.entity.OrderCargoEntity;
import course.QExpress.oms.domain.dto.OrderCargoDTO;

import java.util.List;

/**
 * 货品总重量  服务类
 */
public interface OrderCargoService extends IService<OrderCargoEntity> {


    /**
     * 保存货物信息
     *
     * @param record 货物信息
     * @return 货物信息
     */
    OrderCargoEntity saveSelective(OrderCargoEntity record);

    /**
     * 获取货物列表
     *
     * @param tranOrderId 运单id
     * @param orderId     订单id
     * @return 货物列表
     */
    List<OrderCargoEntity> findAll(Long tranOrderId, Long orderId);

    OrderCargoDTO findByOrderId(Long id);

    /**
     * 最近寄递
     * @param name 物品名称
     * @param memberId 用户ID
     * @return
     */
    List<OrderCargoDTO> listRecent(String name, Long memberId);
}
