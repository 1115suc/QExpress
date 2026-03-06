package course.QExpress.common.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 错误解码器抽象类
 * 实现了 Feign 的 ErrorDecoder 接口，用于统一处理 Feign 客户端调用时的错误响应
 * 子类需要实现 call 方法来定义具体的异常生成逻辑
 */
@Slf4j
public abstract class FeignErrorDecoder implements ErrorDecoder {

    // 抽象方法，由子类实现具体的异常生成逻辑
    public abstract Exception call(int status, int code, String msg);

    /**
     * 解码 Feign 调用错误，将响应转换为异常对象
     *
     * @param methodKey Feign 方法的唯一标识，格式通常为 "类名#方法名"
     * @param response  Feign 调用的响应对象，包含状态码、响应体等信息
     * @return          返回解析后的 Exception 异常对象
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        String message = null;
        try {
            // 读取响应体内容并解析为 JSON 对象，提取错误信息后调用子类的 call 方法
            message = IoUtil.read(response.body().asReader(CharsetUtil.CHARSET_UTF_8));
            log.info("methodKey {} response {}", methodKey, message);
            JSONObject jsonObject = JSONUtil.parseObj(message);
            return this.call(response.status(), jsonObject.getInt("code"), jsonObject.getStr("msg"));
        } catch (Exception e) {
            // 出现网络中断、服务宕机等异常情况时，构造默认错误信息并调用子类的 call 方法
            String msg = StrUtil.format("Feign 调用失败，methodKey = {}, message = {}", methodKey, message);
            return this.call(response.status(), -1, msg);
        }
    }
}
