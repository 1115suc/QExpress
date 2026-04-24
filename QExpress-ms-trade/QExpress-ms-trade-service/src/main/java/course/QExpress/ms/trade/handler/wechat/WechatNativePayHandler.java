package course.QExpress.ms.trade.handler.wechat;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import course.QExpress.common.exception.QEException;
import course.QExpress.ms.trade.annotation.PayChannel;
import course.QExpress.ms.trade.entity.TradingEntity;
import course.QExpress.ms.trade.enums.PayChannelEnum;
import course.QExpress.ms.trade.enums.TradingEnum;
import course.QExpress.ms.trade.enums.TradingStateEnum;
import course.QExpress.ms.trade.handler.NativePayHandler;
import course.QExpress.ms.trade.handler.wechat.response.WeChatResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信二维码支付
 *
 * @author zzj
 * @version 1.0
 */
@Component("wechatNativePayHandler")
@PayChannel(type = PayChannelEnum.WECHAT_PAY)
public class WechatNativePayHandler implements NativePayHandler {

    @Override
    public void createDownLineTrading(TradingEntity tradingEntity) throws QEException {
        // 查询配置
        WechatPayHttpClient client = WechatPayHttpClient.get(tradingEntity.getEnterpriseId());
        //请求地址
        String apiPath = "/v3/pay/transactions/native";

        //请求参数
        Map<String, Object> params = MapUtil.<String, Object>builder()
                .put("mchid", client.getMchId())
                .put("appid", client.getAppId())
                .put("description", tradingEntity.getMemo())
                .put("notify_url", client.getNotifyUrl())
                .put("out_trade_no", Convert.toStr(tradingEntity.getTradingOrderNo()))
                .put("amount", MapUtil.<String, Object>builder()
                        .put("total", Convert.toInt(NumberUtil.mul(tradingEntity.getTradingAmount(), 100))) //金额，单位：分
                        .put("currency", "CNY") //人民币
                        .build())
                .build();

        try {
            WeChatResponse response = client.doPost(apiPath, params);
            if (!response.isOk()) {
                //下单失败
                throw new QEException(TradingEnum.NATIVE_PAY_FAIL);
            }
            //指定统一下单code
            tradingEntity.setPlaceOrderCode(Convert.toStr(response.getStatus()));
            //二维码需要展现的信息
            tradingEntity.setPlaceOrderMsg(JSONUtil.parseObj(response.getBody()).getStr("code_url"));
            //指定统一下单json字符串
            tradingEntity.setPlaceOrderJson(JSONUtil.toJsonStr(response));
            //指定交易状态
            tradingEntity.setTradingState(TradingStateEnum.FKZ);
        } catch (Exception e) {
            throw new QEException(TradingEnum.NATIVE_PAY_FAIL);
        }
    }
}
