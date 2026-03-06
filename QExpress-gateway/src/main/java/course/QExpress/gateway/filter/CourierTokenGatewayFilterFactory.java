package course.QExpress.gateway.filter;

import com.itheima.auth.factory.AuthTemplateFactory;
import com.itheima.auth.sdk.AuthTemplate;
import com.itheima.auth.sdk.common.AuthSdkException;
import com.itheima.auth.sdk.dto.AuthUserInfoDTO;
import com.itheima.auth.sdk.service.TokenCheckService;
import course.QExpress.gateway.config.MyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 快递员token拦截处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourierTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> implements AuthFilter {

    private final MyConfig myConfig;
    private final TokenCheckService tokenCheckService;

    @Value("${role.courier}")
    private String roleId;

    @Override
    public GatewayFilter apply(Object config) {
        return new TokenGatewayFilter(this.myConfig, this);
    }

    @Override
    public AuthUserInfoDTO check(String token) {
        try {
            return tokenCheckService.parserToken(token);
        } catch (AuthSdkException e) {
            // 校验失败
        }
        return null;
    }

    @Override
    public Boolean auth(String token, AuthUserInfoDTO authUserInfoDTO, String path) {
        AuthTemplate authTemplate = AuthTemplateFactory.get(token);
        //获取用户拥有的角色id列表
        List<Long> roleIds = authTemplate.opsForRole().findRoleByUserId(authUserInfoDTO.getUserId()).getData();
        log.info("id为：{}的用户拥有的角色id列表：{}，快递员角色id为：{}", authUserInfoDTO.getUserId(), roleIds, roleId);

        //如果用户用拥有快递员角色，则放行
        return roleIds.contains(Long.valueOf(roleId));
    }
}
