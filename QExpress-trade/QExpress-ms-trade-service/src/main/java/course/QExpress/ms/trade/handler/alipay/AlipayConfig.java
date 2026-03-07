package course.QExpress.ms.trade.handler.alipay;

import cn.hutool.extra.spring.SpringUtil;
import com.alipay.easysdk.kernel.Config;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.ms.trade.constant.TradingConstant;
import course.QExpress.ms.trade.entity.PayChannelEntity;
import course.QExpress.ms.trade.enums.TradingEnum;
import course.QExpress.ms.trade.service.PayChannelService;

public class AlipayConfig {

    /**
     * 将支付渠道配置转化为支付宝的配置
     *
     * @param enterpriseId 商户ID
     * @return 支付宝的配置
     */
    public static Config getConfig(Long enterpriseId) {
        // 查询配置
        PayChannelService payChannelService = SpringUtil.getBean(PayChannelService.class);
        PayChannelEntity payChannel = payChannelService.findByEnterpriseId(enterpriseId, TradingConstant.TRADING_CHANNEL_ALI_PAY);

        if (ObjectUtil.isEmpty(payChannel)) {
            throw new QEException(TradingEnum.CONFIG_EMPTY);
        }

        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = payChannel.getDomain();
        config.signType = "RSA2";
        config.appId = payChannel.getAppId();
        //配置应用私钥
        config.merchantPrivateKey = payChannel.getMerchantPrivateKey();
        //配置支付宝公钥
        config.alipayPublicKey = payChannel.getPublicKey();
        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = payChannel.getNotifyUrl();
        //设置AES密钥，调用AES加解密相关接口时需要（可选）
        config.encryptKey = payChannel.getEncryptKey();
        return config;
    }

}
