package course.QExpress.ms.trade.api.fallback;

import course.QExpress.ms.trade.api.NativePayFeign;
import course.QExpress.ms.trade.domain.request.NativePayDTO;
import course.QExpress.ms.trade.domain.response.NativePayResponseDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

public class NativePayFeignFallbackFactory implements FallbackFactory<NativePayFeign> {
    @Override
    public NativePayFeign create(Throwable cause) {
        return new NativePayFeign() {
            @Override
            public NativePayResponseDTO createDownLineTrading(NativePayDTO nativePayDTO) {
                return null;
            }

            @Override
            public String queryQrCode(Long tradingOrderNo) {
                return null;
            }
        };
    }
}