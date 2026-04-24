package course.QExpress.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.oms.entity.OrderLocationEntity;
import course.QExpress.oms.mapper.OrderLocationMapper;
import course.QExpress.oms.service.OrderLocationService;
import org.springframework.stereotype.Service;

/**
 * 位置信息服务实现
 */
@Service
public class OrderLocationServiceImpl extends ServiceImpl<OrderLocationMapper, OrderLocationEntity>
        implements OrderLocationService {

}
