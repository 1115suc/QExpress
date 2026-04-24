package course.QExpress.web.driver.service;

import course.QExpress.web.driver.vo.request.AccountLoginVO;

public interface LoginService {
    /**
     * 账号登录
     *
     * @param accountLoginVO 账号登录请求
     * @return token
     */
    String accountLogin(AccountLoginVO accountLoginVO);
}
