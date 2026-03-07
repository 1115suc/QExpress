package course.QExpress.ms.trade.service.Impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.ms.trade.constant.Constants;
import course.QExpress.ms.trade.entity.TradingEntity;
import course.QExpress.ms.trade.enums.TradingStateEnum;
import course.QExpress.ms.trade.mapper.TradingMapper;
import course.QExpress.ms.trade.service.TradingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：交易订单表 服务实现类
 */
@Service
public class TradingServiceImpl extends ServiceImpl<TradingMapper, TradingEntity> implements TradingService {

    @Override
    public TradingEntity findTradByTradingOrderNo(Long tradingOrderNo) {
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getTradingOrderNo, tradingOrderNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public TradingEntity findTradByProductOrderNo(Long productOrderNo) {
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getProductOrderNo, productOrderNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public List<TradingEntity> findListByTradingState(TradingStateEnum tradingState, Integer count) {
        count = NumberUtil.max(count, 10);
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getTradingState, tradingState)
                .eq(TradingEntity::getEnableFlag, Constants.YES)
                .orderByAsc(TradingEntity::getCreated)
                .last("LIMIT " + count);
        return list(queryWrapper);
    }
}
