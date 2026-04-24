package course.QExpress.ms.trade.annotation;

import course.QExpress.ms.trade.enums.PayChannelEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented //标记注解
public @interface PayChannel {

    PayChannelEnum type();

}