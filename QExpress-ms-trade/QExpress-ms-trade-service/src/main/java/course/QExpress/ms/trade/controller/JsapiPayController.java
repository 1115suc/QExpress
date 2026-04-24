package course.QExpress.ms.trade.controller;

import course.QExpress.common.util.BeanUtil;
import course.QExpress.ms.trade.domain.request.JsapiPayDTO;
import course.QExpress.ms.trade.domain.response.JsapiPayResponseDTO;
import course.QExpress.ms.trade.entity.TradingEntity;
import course.QExpress.ms.trade.service.JsapiPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Native支付方式Face接口：商户生成二维码，用户扫描支付
 */
@RequestMapping("jsapi")
@RestController
@RequiredArgsConstructor
@Api(tags = "Jsapi支付")
public class JsapiPayController {

    private final JsapiPayService jsapiPayService;

    /***
     * 统一jsapi交易预创建
     * 商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易会话标识后再按Native、
     * JSAPI、APP等不同场景生成交易串调起支付。
     * @param jsapiPayDTO jsapi提交支付请求对象
     *
     * @return 交易单，支付串码
     */
    @PostMapping
    @ApiOperation(value = "jsapi预交易", notes = "jsapi预交易")
    @ApiImplicitParam(name = "jsapiPayDTO", value = "交易单", required = true)
    public JsapiPayResponseDTO createJsapiTrading(@RequestBody JsapiPayDTO jsapiPayDTO) {
        TradingEntity tradingEntity = BeanUtil.toBean(jsapiPayDTO, TradingEntity.class);
        TradingEntity trading = this.jsapiPayService.createJsapiTrading(tradingEntity);
        return BeanUtil.toBean(trading, JsapiPayResponseDTO.class);
    }

}
