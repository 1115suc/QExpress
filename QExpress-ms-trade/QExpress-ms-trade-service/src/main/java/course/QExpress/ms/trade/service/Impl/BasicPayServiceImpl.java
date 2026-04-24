package course.QExpress.ms.trade.service.Impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.NumberUtil;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.BeanUtil;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.ms.trade.constant.Constants;
import course.QExpress.ms.trade.constant.TradingCacheConstant;
import course.QExpress.ms.trade.domain.RefundRecordDTO;
import course.QExpress.ms.trade.domain.TradingDTO;
import course.QExpress.ms.trade.entity.RefundRecordEntity;
import course.QExpress.ms.trade.entity.TradingEntity;
import course.QExpress.ms.trade.enums.TradingEnum;
import course.QExpress.ms.trade.enums.TradingStateEnum;
import course.QExpress.ms.trade.handler.BasicPayHandler;
import course.QExpress.ms.trade.handler.BeforePayHandler;
import course.QExpress.ms.trade.handler.HandlerFactory;
import course.QExpress.ms.trade.service.BasicPayService;
import course.QExpress.ms.trade.service.RefundRecordService;
import course.QExpress.ms.trade.service.TradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 支付的基础功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicPayServiceImpl implements BasicPayService {

    private final BeforePayHandler beforePayHandler;
    private final RedissonClient redissonClient;
    private final TradingService tradingService;
    private final RefundRecordService refundRecordService;

    @Override
    public TradingDTO queryTrading(Long tradingOrderNo) throws QEException {
        //通过单号查询交易单数据
        TradingEntity trading = this.tradingService.findTradByTradingOrderNo(tradingOrderNo);
        //查询前置处理：检测交易单参数
        this.beforePayHandler.checkQueryTrading(trading);

        String key = TradingCacheConstant.QUERY_PAY + tradingOrderNo;
        RLock lock = redissonClient.getFairLock(key);
        try {
            //获取锁
            if (lock.tryLock(TradingCacheConstant.REDIS_WAIT_TIME, TimeUnit.SECONDS)) {
                //选取不同的支付渠道实现
                BasicPayHandler handler = HandlerFactory.get(trading.getTradingChannel(), BasicPayHandler.class);
                Boolean result = handler.queryTrading(trading);
                if (result) {
                    //如果交易单已经完成，需要将二维码数据删除，节省数据库空间，如果有需要可以再次生成
                    if (ObjectUtil.equalsAny(trading.getTradingState(), TradingStateEnum.YJS, TradingStateEnum.QXDD)) {
                        trading.setQrCode("");
                    }
                    //更新数据
                    this.tradingService.saveOrUpdate(trading);
                }
                return BeanUtil.toBean(trading, TradingDTO.class);
            }
            throw new QEException(TradingEnum.NATIVE_QUERY_FAIL);
        } catch (QEException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询交易单数据异常: trading = {}", trading, e);
            throw new QEException(TradingEnum.NATIVE_QUERY_FAIL);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Boolean refundTrading(Long tradingOrderNo, BigDecimal refundAmount) throws QEException {
        //通过单号查询交易单数据
        TradingEntity trading = this.tradingService.findTradByTradingOrderNo(tradingOrderNo);
        //设置退款金额
        trading.setRefund(NumberUtil.add(refundAmount, trading.getRefund()));

        //入库前置检查
        this.beforePayHandler.checkRefundTrading(trading);

        String key = TradingCacheConstant.REFUND_PAY + tradingOrderNo;
        RLock lock = redissonClient.getFairLock(key);
        try {
            //获取锁
            if (lock.tryLock(TradingCacheConstant.REDIS_WAIT_TIME, TimeUnit.SECONDS)) {
                //幂等性的检查
                RefundRecordEntity refundRecord = this.beforePayHandler.idempotentRefundTrading(trading, refundAmount);
                if (null == refundRecord) {
                    return false;
                }

                //选取不同的支付渠道实现
                BasicPayHandler handler = HandlerFactory.get(refundRecord.getTradingChannel(), BasicPayHandler.class);
                Boolean result = handler.refundTrading(refundRecord);
                if (result) {
                    //更新退款记录数据
                    this.refundRecordService.saveOrUpdate(refundRecord);

                    //设置交易单是退款订单
                    trading.setIsRefund(Constants.YES);
                    this.tradingService.saveOrUpdate(trading);
                }
                return true;
            }
            throw new QEException(TradingEnum.NATIVE_QUERY_FAIL);
        } catch (QEException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询交易单数据异常:{}", ExceptionUtil.stacktraceToString(e));
            throw new QEException(TradingEnum.NATIVE_QUERY_FAIL);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public RefundRecordDTO queryRefundTrading(Long refundNo) throws QEException {
        //通过单号查询交易单数据
        RefundRecordEntity refundRecord = this.refundRecordService.findByRefundNo(refundNo);
        //查询前置处理
        this.beforePayHandler.checkQueryRefundTrading(refundRecord);

        String key = TradingCacheConstant.REFUND_QUERY_PAY + refundNo;
        RLock lock = redissonClient.getFairLock(key);
        try {
            //获取锁
            if (lock.tryLock(TradingCacheConstant.REDIS_WAIT_TIME, TimeUnit.SECONDS)) {

                //选取不同的支付渠道实现
                BasicPayHandler handler = HandlerFactory.get(refundRecord.getTradingChannel(), BasicPayHandler.class);
                Boolean result = handler.queryRefundTrading(refundRecord);
                if (result) {
                    //更新数据
                    this.refundRecordService.saveOrUpdate(refundRecord);
                }
                return BeanUtil.toBean(refundRecord, RefundRecordDTO.class);
            }
            throw new QEException(TradingEnum.REFUND_FAIL);
        } catch (QEException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询退款交易单数据异常: refundRecord = {}", refundRecord, e);
            throw new QEException(TradingEnum.REFUND_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
