package course.QExpress.transport.info.api.fallback;

import course.QExpress.transport.info.api.TransportInfoFeign;
import course.QExpress.transport.info.domain.dto.TransportInfoDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

public class TransportInfoFeignFallbackFactory implements FallbackFactory<TransportInfoFeign> {
    @Override
    public TransportInfoFeign create(Throwable cause) {
        return new TransportInfoFeign() {
            @Override
            public TransportInfoDTO queryByTransportOrderId(String transportOrderId) {
                return null;
            }
        };
    }
}
