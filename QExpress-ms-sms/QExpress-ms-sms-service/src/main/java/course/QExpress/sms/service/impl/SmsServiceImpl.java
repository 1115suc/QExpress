package course.QExpress.sms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.ObjectUtils;
import course.QExpress.sms.domain.dto.SmsInfoDTO;
import course.QExpress.sms.dto.PlatformSmsInfoDTO;
import course.QExpress.sms.entity.SmsRecordEntity;
import course.QExpress.sms.entity.SmsThirdChannelEntity;
import course.QExpress.sms.mapper.SmsRecordMapper;
import course.QExpress.sms.mapper.SmsThirdChannelMapper;
import course.QExpress.sms.service.RouteService;
import course.QExpress.sms.service.SmsService;
import course.QExpress.sms.service.ThirdChannelContainer;
import course.QExpress.sms.service.ThirdSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RouteService routeService;

    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @Autowired
    private SmsThirdChannelMapper smsThirdChannelMapper;

    @Autowired
    private ThirdChannelContainer thirdChannelContainer;

    @Override
    public void singleSend(SmsInfoDTO smsInfoDTO) {
        // todo 参数校验 1.数据校验 2.接口幂等性校验

        //路由短信发送通道
        SmsThirdChannelEntity smsThirdChannelEntity = routeService.route(smsInfoDTO.getBussinessType(), smsInfoDTO.getSmsType(), smsInfoDTO.getContentType());

        if (smsThirdChannelEntity == null) {
            throw new QEException("短信通道不存在");
        }

        //获取service
        ThirdSmsService thirdSmsService = thirdChannelContainer.get(smsThirdChannelEntity.getChannelCode());
        if (thirdSmsService == null) {
            throw new QEException("短信通道不存在");
        }
        long batchId = IdWorker.getId();

        List<SmsRecordEntity> smsRecordEntities = packageEntity(smsInfoDTO, smsThirdChannelEntity.getChannelCode(), batchId);
        log.info("smsRecordEntities : {}", smsRecordEntities);

        //入库
        if (smsRecordMapper.batchInsert(smsRecordEntities) <= 0) { //入库失败
            throw new QEException("发送失败");
        }
        //数据封装
        PlatformSmsInfoDTO platformSmsInfoDTO = packageDTO(batchId, smsInfoDTO, smsThirdChannelEntity);

        //发送短信
        thirdSmsService.send(platformSmsInfoDTO);
    }

    /**
     * 数据封装
     *
     * @return
     */
    private PlatformSmsInfoDTO packageDTO(Long batchId, SmsInfoDTO smsInfoDTO, SmsThirdChannelEntity smsThirdChannelEntity) {

        PlatformSmsInfoDTO platformSmsInfoDTO = new PlatformSmsInfoDTO();
        platformSmsInfoDTO.setId(batchId); // 短信发送唯一id,也就是批次id
        platformSmsInfoDTO.setMobiles(smsInfoDTO.getMobiles()); //发送手机号
        platformSmsInfoDTO.setContent(String.format(smsThirdChannelEntity.getSmsTemplate(), smsInfoDTO.getVerifyCode())); //短信验证码
        platformSmsInfoDTO.setSignName(smsThirdChannelEntity.getSignName()); //签名
        platformSmsInfoDTO.setThirdTemplateCode(smsThirdChannelEntity.getThirdTemplateCode()); //第三方模板code
        platformSmsInfoDTO.setUserId(smsInfoDTO.getUserId()); //发送短信用户id
        platformSmsInfoDTO.setVerifyCode(smsInfoDTO.getVerifyCode());

        return platformSmsInfoDTO;
    }

    private List<SmsRecordEntity> packageEntity(SmsInfoDTO smsInfoDTO, String channelCode, Long batchId) {

        List<SmsRecordEntity> smsRecordEntities = new ArrayList<>();
        for (String mobile : smsInfoDTO.getMobiles().split(",")) {
            SmsRecordEntity smsRecordEntity = ObjectUtils.convert(smsInfoDTO, SmsRecordEntity.class, (infoDTO, entity) -> {
                entity.setThirdChannelCode(channelCode);
                entity.setId(IdWorker.getId());
                entity.setCreater(infoDTO.getUserId());
                entity.setUpdater(infoDTO.getUserId());
                entity.setBatchId(batchId);
                entity.setMobile(mobile);
                entity.setCreated(LocalDateTime.now());
                entity.setUpdated(LocalDateTime.now());
            });
            smsRecordEntities.add(smsRecordEntity);
        }
        return smsRecordEntities;
    }

}
