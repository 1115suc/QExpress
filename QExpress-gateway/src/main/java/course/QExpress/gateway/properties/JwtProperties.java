package course.QExpress.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qe.jwt")
@Data
public class JwtProperties {

    private String publicKey;
}