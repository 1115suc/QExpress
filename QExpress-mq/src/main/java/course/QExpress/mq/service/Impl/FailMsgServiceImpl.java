package course.QExpress.mq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.mq.entity.FailMsgEntity;
import course.QExpress.mq.mapper.FailMsgMapper;
import course.QExpress.mq.service.FailMsgService;
import course.QExpress.mq.service.MQService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(MQService.class)
public class FailMsgServiceImpl extends ServiceImpl<FailMsgMapper, FailMsgEntity>
        implements FailMsgService {
}