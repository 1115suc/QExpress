package course.QExpress.web.driver.config;

import course.QExpress.common.properties.AliOSSProperties;
import course.QExpress.common.util.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置对象存储工具类
 * 主要用于上传图片和文件
 */
@Configuration
@Slf4j
public class OSSConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOSSUtil aliOSSUtil(AliOSSProperties aliOSSProperties) {
        log.info("创建阿里云OSS工具类...");

        return new AliOSSUtil(
                aliOSSProperties.getEndpoint(),
                aliOSSProperties.getAccessKeyId(),
                aliOSSProperties.getAccessKeySecret(),
                aliOSSProperties.getBucketName());
    }
}
