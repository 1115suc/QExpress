package course.QExpress.common.config;

import course.QExpress.common.exception.QEException;

/**
 * 通用 Feign 错误解码器类
 * 用于处理 Feign 客户端调用时的错误响应，将错误信息转换为 QEException 异常
 */
public class CommonFeignErrorDecoder extends FeignErrorDecoder {

    /**
     * 解码 Feign 调用错误，生成对应的异常对象
     *
     * @param status HTTP 状态码，表示请求的响应状态（如 400、500 等）
     * @param code   业务错误码，用于标识具体的业务错误类型
     * @param msg    错误消息描述，包含错误的详细信息
     * @return       返回 QEException 异常对象，封装了错误的所有信息
     */
    @Override
    public Exception call(int status, int code, String msg) {
        return new QEException(msg, code, status);
    }
}