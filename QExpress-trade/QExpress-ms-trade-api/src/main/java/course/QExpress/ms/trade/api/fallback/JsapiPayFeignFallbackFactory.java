package course.QExpress.ms.trade.api.fallback;

import course.QExpress.ms.trade.api.JsapiPayFeign;
import course.QExpress.ms.trade.domain.request.JsapiPayDTO;
import course.QExpress.ms.trade.domain.response.JsapiPayResponseDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

public class JsapiPayFeignFallbackFactory implements FallbackFactory<JsapiPayFeign> {
    @Override
    public JsapiPayFeign create(Throwable cause) {
        return new JsapiPayFeign() {
            @Override
            public JsapiPayResponseDTO createJsapiTrading(JsapiPayDTO jsapiPayDTO) {
                return null;
            }
        };
    }
}