package course.QExpress.ms.trade.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import course.QExpress.common.exception.QEException;
import course.QExpress.ms.trade.domain.TradingDTO;
import course.QExpress.ms.trade.entity.TradingEntity;
import course.QExpress.ms.trade.enums.TradingEnum;
import course.QExpress.ms.trade.service.TradingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = "交易单服务")
@RequiredArgsConstructor
@RequestMapping("tradings")
public class TradingController {

    private final TradingService tradingService;

    /**
     * 根据业务系统订单号 或 交易单号查询交易单 （二个至少传递一个，优先按照交易单号查询）
     *
     * @param productOrderNo 业务订单号
     * @param tradingOrderNo 交易单号
     * @return 交易单数据
     */
    @GetMapping
    @ApiOperation(value = "查询交易单", notes = "根据业务系统订单号 或 交易单号查询交易单 （二个至少传递一个，优先按照交易单号查询）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productOrderNo", value = "业务订单号"),
            @ApiImplicitParam(name = "tradingOrderNo", value = "交易单号")
    })
    public TradingDTO queryTrading(@RequestParam(value = "productOrderNo", required = false) Long productOrderNo,
                                   @RequestParam(value = "tradingOrderNo", required = false) Long tradingOrderNo) {
        if (ObjectUtil.isAllEmpty(productOrderNo, tradingOrderNo)) {
            throw new QEException(TradingEnum.TRADING_QUERY_PARAM_ERROR);
        }
        TradingEntity tradingEntity;
        if (ObjectUtil.isNotEmpty(tradingOrderNo)) {
            tradingEntity = this.tradingService.findTradByTradingOrderNo(tradingOrderNo);
        } else {
            tradingEntity = this.tradingService.findTradByProductOrderNo(productOrderNo);
        }
        if (ObjectUtil.isEmpty(tradingEntity)) {
            throw new QEException(TradingEnum.NOT_FOUND);
        }
        return BeanUtil.toBean(tradingEntity, TradingDTO.class);
    }

}
