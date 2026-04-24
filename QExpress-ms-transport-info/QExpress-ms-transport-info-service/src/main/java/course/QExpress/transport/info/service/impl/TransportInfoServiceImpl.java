package course.QExpress.transport.info.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.github.benmanes.caffeine.cache.Cache;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.transport.info.config.RedisConfig;
import course.QExpress.transport.info.domain.dto.TransportInfoDTO;
import course.QExpress.transport.info.entity.TransportInfoDetail;
import course.QExpress.transport.info.entity.TransportInfoEntity;
import course.QExpress.transport.info.enums.ExceptionEnum;
import course.QExpress.transport.info.service.TransportInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName 类名
 * @Description 类说明
 */
@Slf4j
@Service
public class TransportInfoServiceImpl implements TransportInfoService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Cache<String, TransportInfoDTO> transportInfoCache;

    @CachePut(value = "transport-info", key = "#p0") //更新缓存数据
    @Override
    public TransportInfoEntity saveOrUpdate(String transportOrderId, TransportInfoDetail infoDetail) {
        // TODO day10 保存或修改 物流信息

        //1. 根据运单id查询物流信息
        Query query = Query.query(Criteria.where("transportOrderId").is(transportOrderId)); //构造查询条件
        TransportInfoEntity transportInfoEntity = mongoTemplate.findOne(query, TransportInfoEntity.class);
        //2. 如果物流信息为空 创建物流信息
        if (ObjectUtil.isEmpty(transportInfoEntity)) {
            //运单信息不存在，新增数据
            transportInfoEntity = new TransportInfoEntity();
            transportInfoEntity.setTransportOrderId(transportOrderId);
            transportInfoEntity.setInfoList(ListUtil.toList(infoDetail));
            transportInfoEntity.setCreated(System.currentTimeMillis());
        }else{
            //3. 如果物流信息存在 将信息加入到集合中
            transportInfoEntity.getInfoList().add(infoDetail);
        }

        //4. 无论新增还是修改 都设置updated时间
        transportInfoEntity.setUpdated(System.currentTimeMillis());

        // 缓存一致性
        // this.transportInfoCache.invalidate(transportOrderId);
        this.stringRedisTemplate.convertAndSend(RedisConfig.CHANNEL_TOPIC, transportOrderId);

        //5. 保存/更新到MongoDB
        mongoTemplate.save(transportInfoEntity);
        // TODO day10 后续完善一级缓存 基于Redis发布订阅更新功能
        return transportInfoEntity;
    }

    // TODO day10 基于Spring Cache实现Redis二级缓存
    @Cacheable(value = "transport-info", key = "#p0") //新增缓存数据
    @Override
    public TransportInfoEntity queryByTransportOrderId(String transportOrderId) {
        // TODO day10 查询物流信息

        //根据运单id查询
        Query query = Query.query(Criteria.where("transportOrderId").is(transportOrderId));
        TransportInfoEntity transportInfoEntity = mongoTemplate.findOne(query, TransportInfoEntity.class);
        if (ObjectUtil.isEmpty(transportInfoEntity)) {
            throw new QEException(ExceptionEnum.NOT_FOUND);
        }
        return transportInfoEntity;

    }
}
