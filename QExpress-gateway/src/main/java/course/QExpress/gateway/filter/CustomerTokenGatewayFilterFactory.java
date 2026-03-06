package course.QExpress.gateway.filter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.itheima.auth.sdk.dto.AuthUserInfoDTO;
import course.QExpress.common.constant.Constants;
import course.QExpress.common.util.JwtUtils;
import course.QExpress.gateway.config.MyConfig;
import course.QExpress.gateway.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 快递员token拦截处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> implements AuthFilter {

    private final MyConfig myConfig;
    private final JwtProperties jwtProperties;

    @Override
    public GatewayFilter apply(Object config) {
        return new TokenGatewayFilter(this.myConfig, this);
    }

    @Override
    public AuthUserInfoDTO check(String token) {
        // 普通用户的token没有对接权限系统，需要自定实现
        // 鉴权逻辑在用户端自行实现 网关统一放行
        log.info("开始解析token {}", token);
        Map<String, Object> claims = JwtUtils.checkToken(token, jwtProperties.getPublicKey());
        if (ObjectUtil.isEmpty(claims)) {
            //token失效
            return null;
        }

        Long userId = MapUtil.get(claims, Constants.GATEWAY.USER_ID, Long.class);
        //token解析成功，放行
        AuthUserInfoDTO authUserInfoDTO = new AuthUserInfoDTO();
        authUserInfoDTO.setUserId(userId);
        return authUserInfoDTO;
    }

    @Override
    public Boolean auth(String token, AuthUserInfoDTO authUserInfoDTO, String path) {
        //普通用户不需要校验角色
        return true;
    }

    @Override
    public String tokenHeaderName() {
        return Constants.GATEWAY.ACCESS_TOKEN;
    }
}
