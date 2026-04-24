package course.QExpress.sms.service;

import course.QExpress.sms.domain.dto.SmsInfoDTO;

public interface SmsService {

    /**
     * 单个手机号发送短信验证码接口
     *
     * @param smsInfoDTO
     */
    void singleSend(SmsInfoDTO smsInfoDTO);
}
