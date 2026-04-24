package course.QExpress.sms.service.impl;

import course.QExpress.sms.dto.PlatformSmsInfoDTO;
import course.QExpress.sms.service.ThirdSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("aliyun")
@Slf4j
public class AliyunThirdSmsServiceImpl implements ThirdSmsService {
    @Override
    public void send(PlatformSmsInfoDTO platformSmsInfoDTO) {
        //第三方发送短信验证码
        log.info("短信发送成功 ...");
        log.info("platformSmsInfoDTO：{}", platformSmsInfoDTO);

    }
}
