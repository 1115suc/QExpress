package course.QExpress.gateway.filter;

import com.google.common.collect.Lists;
import com.itheima.auth.factory.AuthTemplateFactory;
import com.itheima.auth.sdk.AuthTemplate;
import com.itheima.auth.sdk.common.AuthSdkException;
import com.itheima.auth.sdk.dto.AuthUserInfoDTO;
import com.itheima.auth.sdk.service.TokenCheckService;
import course.QExpress.gateway.config.MyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理员token拦截处理
 */
@Component
@RequiredArgsConstructor
public class ManagerTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> implements AuthFilter {

    private final MyConfig myConfig;
    private final TokenCheckService tokenCheckService;

    @Value("${role.manager}")
    private String roleId;

    @Override
    public GatewayFilter apply(Object config) {
        return new TokenGatewayFilter(this.myConfig, this);
    }

    @Override
    public AuthUserInfoDTO check(String token) {
        try {
            return this.tokenCheckService.parserToken(token);
        } catch (AuthSdkException e) {
            // 校验失败
        }
        return null;
    }

    @Override
    public Boolean auth(String token, AuthUserInfoDTO authUserInfoDTO, String path) {
        // 校验是否为后台管理员角色
        AuthTemplate authTemplate = AuthTemplateFactory.get(token);
        //获取用户拥有的角色id列表
        List<Long> roleIds = authTemplate.opsForRole().findRoleByUserId(authUserInfoDTO.getUserId()).getData();

        //如果用户用拥有管理员角色，则放行
        List<Long> roleIdList = Lists.newArrayList(roleId.split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
        roleIdList.retainAll(roleIds);
        return roleIdList.size() > 0;
    }
}
