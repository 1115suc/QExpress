package course.QExpress.sms.service.impl;

import course.QExpress.sms.service.ThirdChannelContainer;
import course.QExpress.sms.service.ThirdSmsService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ThirdChannelContainerImpl implements ThirdChannelContainer {
    private Map<String, ThirdSmsService> smsServiceContainer = new HashMap<>();

    @Override
    public void put(String code, ThirdSmsService thirdSmsService) {
        smsServiceContainer.put(code, thirdSmsService);
    }

    @Override
    public ThirdSmsService get(String code) {
        return smsServiceContainer.get(code);
    }
}
