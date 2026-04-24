package course.QExpress.ms.trade.api.fallback;

import course.QExpress.ms.trade.api.TradingFeign;
import course.QExpress.ms.trade.domain.TradingDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

public class TradingFeignFallbackFactory implements FallbackFactory<TradingFeign> {
    @Override
    public TradingFeign create(Throwable cause) {
        return new TradingFeign() {
            @Override
            public TradingDTO queryTrading(Long productOrderNo, Long tradingOrderNo) {
                return null;
            }
        };
    }
}