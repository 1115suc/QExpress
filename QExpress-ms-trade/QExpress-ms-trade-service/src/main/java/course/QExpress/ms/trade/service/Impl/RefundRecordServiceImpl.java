package course.QExpress.ms.trade.service.Impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.ms.trade.entity.RefundRecordEntity;
import course.QExpress.ms.trade.enums.RefundStatusEnum;
import course.QExpress.ms.trade.mapper.RefundRecordMapper;
import course.QExpress.ms.trade.service.RefundRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description： 退款记录服务实现类
 */
@Service
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecordEntity> implements RefundRecordService {

    @Override
    public RefundRecordEntity findByRefundNo(Long refundNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getRefundNo, refundNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public List<RefundRecordEntity> findListByTradingOrderNo(Long tradingOrderNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getTradingOrderNo, tradingOrderNo);
        queryWrapper.orderByDesc(RefundRecordEntity::getCreated);
        return super.list(queryWrapper);
    }

    @Override
    public List<RefundRecordEntity> findListByProductOrderNo(Long productOrderNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getProductOrderNo, productOrderNo);
        queryWrapper.orderByDesc(RefundRecordEntity::getCreated);
        return super.list(queryWrapper);
    }

    @Override
    public List<RefundRecordEntity> findListByRefundStatus(RefundStatusEnum refundStatus, Integer count) {
        count = NumberUtil.max(count, 10);
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getRefundStatus, refundStatus)
                .orderByAsc(RefundRecordEntity::getCreated)
                .last("LIMIT " + count);
        return list(queryWrapper);
    }
}
