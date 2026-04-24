package course.QExpress.sms.handler;

import course.QExpress.sms.service.ThirdChannelContainer;
import course.QExpress.sms.service.ThirdSmsService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 项目启动时将ThirdSmsService的实现类加入到thirdChannelContainer中
 */
@Component
public class ThirdSmsPostProcessor implements BeanPostProcessor {

    @Autowired
    private ThirdChannelContainer thirdChannelContainer;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThirdSmsService) {
            thirdChannelContainer.put(beanName, (ThirdSmsService) bean);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
