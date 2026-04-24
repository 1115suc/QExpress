package course.QExpress.sms.service;

import course.QExpress.sms.entity.SmsThirdChannelEntity;

public interface RouteService {

    SmsThirdChannelEntity route(Integer bussinessType, Integer smsType, Integer contentType);
}
