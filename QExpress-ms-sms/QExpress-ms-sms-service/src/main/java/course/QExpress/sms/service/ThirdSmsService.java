package course.QExpress.sms.service;

import course.QExpress.sms.dto.PlatformSmsInfoDTO;

/**
 * 第三方接口对接平台
 */
public interface ThirdSmsService {

    /**
     * 发送短信
     * @param platformSmsInfoDTO
     */
    void send(PlatformSmsInfoDTO platformSmsInfoDTO);


}
