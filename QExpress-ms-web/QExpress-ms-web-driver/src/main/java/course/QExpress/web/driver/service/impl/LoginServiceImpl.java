package course.QExpress.web.driver.service.impl;

import com.itheima.auth.sdk.AuthTemplate;
import com.itheima.auth.sdk.common.Result;
import com.itheima.auth.sdk.dto.LoginDTO;
import course.QExpress.common.exception.QEWebException;
import course.QExpress.web.driver.service.LoginService;
import course.QExpress.web.driver.vo.request.AccountLoginVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private AuthTemplate authTemplate;

    @Override
    public String accountLogin(AccountLoginVO accountLoginVO) {
        //账号密码登录
        Result<LoginDTO> loginResult = authTemplate.opsForLogin().token(accountLoginVO.getAccount(), accountLoginVO.getPassword());

        //校验登录是否成功
        if (loginResult.getCode() != Result.success().getCode()) {
            throw new QEWebException("登录失败");
        }
        return loginResult.getData().getToken().getToken();
    }
}
