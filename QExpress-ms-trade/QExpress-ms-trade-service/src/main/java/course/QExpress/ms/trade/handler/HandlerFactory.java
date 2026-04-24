package course.QExpress.ms.trade.handler;

import cn.hutool.extra.spring.SpringUtil;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.ms.trade.annotation.PayChannel;
import course.QExpress.ms.trade.enums.PayChannelEnum;

import java.util.Map;

/**
 * Handler工厂，用于获取指定类型的具体渠道的实例对象
 */
public class HandlerFactory {

    private HandlerFactory() {

    }

    /**
     * 根据支付渠道枚举获取对应的处理器实例
     *
     * @param payChannel 支付渠道枚举，用于指定需要获取的支付渠道类型（如支付宝、微信支付等）
     * @param handler 处理器的 Class 类型，指定需要获取的处理器接口或抽象类的类型
     * @param <T> 泛型类型，表示处理器的具体类型
     * @return 返回匹配指定支付渠道的处理器实例，如果未找到则返回 null
     */
    public static <T> T get(PayChannelEnum payChannel, Class<T> handler) {
        // 从 Spring 容器中获取所有指定类型的处理器 Bean
        Map<String, T> beans = SpringUtil.getBeansOfType(handler);
        // 遍历所有处理器 Bean，查找与指定支付渠道匹配的处理器
        for (Map.Entry<String, T> entry : beans.entrySet()) {
            // 获取处理器类上的支付渠道注解
            PayChannel payChannelAnnotation = entry.getValue().getClass().getAnnotation(PayChannel.class);
            // 判断注解是否存在且注解中的渠道类型与传入的渠道枚举相匹配
            if (ObjectUtil.isNotEmpty(payChannelAnnotation) && ObjectUtil.equal(payChannel, payChannelAnnotation.type())) {
                return entry.getValue();
            }
        }
        // 未找到匹配的处理器时返回 null
        return null;
    }


    public static <T> T get(String payChannel, Class<T> handler) {
        return get(PayChannelEnum.valueOf(payChannel), handler);
    }
}
