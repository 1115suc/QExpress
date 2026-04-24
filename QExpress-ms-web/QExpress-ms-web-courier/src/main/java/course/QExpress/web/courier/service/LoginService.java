package course.QExpress.web.courier.service;

import course.QExpress.common.vo.R;
import course.QExpress.web.courier.vo.login.AccountLoginVO;
import course.QExpress.web.courier.vo.login.LoginVO;

public interface LoginService {

    /**
     * 根据用户名和密码进行登录
     *
     * @param accountLoginVO 登录信息
     * @return token
     */
    R<LoginVO> accountLogin(AccountLoginVO accountLoginVO);
}
