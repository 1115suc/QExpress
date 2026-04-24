package course.QExpress.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.oms.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单状态  Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

}
