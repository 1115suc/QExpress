package course.QExpress.web.manager.config;

import course.QExpress.common.config.FeignErrorDecoder;
import course.QExpress.common.exception.QEWebException;
import org.springframework.context.annotation.Configuration;

/**
 * web调用feign失败解码器实现
 *
 * @author zzj
 * @version 1.0
 */
@Configuration
public class WebFeignErrorDecoder extends FeignErrorDecoder {

    @Override
    public Exception call(int status, int code, String msg) {
        return new QEWebException(msg);
    }
}
