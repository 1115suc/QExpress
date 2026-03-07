package course.QExpress.ms.trade.handler;

import course.QExpress.ms.trade.entity.TradingEntity;

/**
 * jsapi下单处理
 */
public interface JsapiPayHandler {

    /**
     * 创建交易
     *
     * @param tradingEntity 交易单
     */
    void createJsapiTrading(TradingEntity tradingEntity);
}
