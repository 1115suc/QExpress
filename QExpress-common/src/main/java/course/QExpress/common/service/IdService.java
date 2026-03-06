package course.QExpress.common.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import course.QExpress.common.enums.IdEnum;
import course.QExpress.common.exception.QEException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * id服务，用于生成自定义的id
 */
@Service
public class IdService {

    @Value("${qe.id.leaf:}")
    private String leafUrl;

    /**
     * 生成自定义id
     *
     * @param idEnum id配置
     * @return id值
     */
    public String getId(IdEnum idEnum) {
        String idStr = this.doGet(idEnum);
        return idEnum.getPrefix() + idStr;
    }

    private String doGet(IdEnum idEnum) {
        if (StrUtil.isEmpty(this.leafUrl)) {
            throw new QEException("qe.id.leaf配置不能为空.");
        }
        //访问leaf服务获取id
        String url = StrUtil.format("{}/api/{}/get/{}", this.leafUrl, idEnum.getType(), idEnum.getBiz());
        //设置超时时间为10s
        HttpResponse httpResponse = HttpRequest.get(url)
                .setReadTimeout(10000)
                .execute();
        if (httpResponse.isOk()) {
            return httpResponse.body();
        }
        throw new QEException(StrUtil.format("访问leaf服务出错，leafUrl = {}, idEnum = {}", this.leafUrl, idEnum));
    }

}
