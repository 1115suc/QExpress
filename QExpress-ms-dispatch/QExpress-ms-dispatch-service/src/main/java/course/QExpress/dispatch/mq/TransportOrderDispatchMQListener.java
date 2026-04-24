package course.QExpress.dispatch.mq;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import course.QExpress.common.constant.Constants;
import course.QExpress.dispatch.dto.DispatchMsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 对于待调度运单消息的处理
 */
@Slf4j
@Component
public class TransportOrderDispatchMQListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理消息，合并运单到Redis队列
     *
     * @param msg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Constants.MQ.Queues.DISPATCH_MERGE_TRANSPORT_ORDER),
            exchange = @Exchange(name = Constants.MQ.Exchanges.TRANSPORT_ORDER_DELAYED, type = ExchangeTypes.TOPIC, delayed = Constants.MQ.DELAYED),
            key = Constants.MQ.RoutingKeys.JOIN_DISPATCH
    ))
    public void listenDispatchMsg(String msg) {
        // {"transportOrderId":"SL1000000000560","currentAgencyId":100280,"nextAgencyId":90001,"totalWeight":3.5,"totalVolume":2.1,"created":1652337676330}
        log.info("接收到新运单的消息 >>> msg = {}", msg);
        DispatchMsgDTO dispatchMsgDTO = JSONUtil.toBean(msg, DispatchMsgDTO.class);
        if (ObjectUtil.isEmpty(dispatchMsgDTO)) {
            return;
        }

        // 1. 获取 当前网点id  下一站网点id
        Long startId = dispatchMsgDTO.getCurrentAgencyId();
        Long endId = dispatchMsgDTO.getNextAgencyId();
        // 2. 获取运单id
        String transportOrderId = dispatchMsgDTO.getTransportOrderId();
        //  (说明: 消息幂等性处理，将相同起始节点的运单存放到set结构的redis中，在相应的运单处理完成后将其删除掉)
        // 3. 获取redis中set集合的key
        String setRedisKey = this.getSetRedisKey(startId, endId);
        // 4. 判断当前的运单任务 在redis的set集合中是否已经存在  tips: 使用set集合的isMember方法
        if (this.stringRedisTemplate.opsForSet().isMember(setRedisKey, transportOrderId)) {
            //重复消息
            return;
        }
        // 5. 获取redis中list集合的key

        // (说明: 存储数据到redis，采用list结构，从左边写入数据，读取数据时从右边读取)
        // (要存的value值格式==>{"transportOrderId":111222, "totalVolume":0.8, "totalWeight":2.1, "created":111222223333})
        // 6. 构建value值 基于map即可，然后转为json字符串
        // 7. 将构建的value值 从左push到list集合中
        String listRedisKey = this.getListRedisKey(startId, endId);
        String value = JSONUtil.toJsonStr(MapUtil.builder()
                .put("transportOrderId", transportOrderId)
                .put("totalVolume", dispatchMsgDTO.getTotalVolume())
                .put("totalWeight", dispatchMsgDTO.getTotalWeight())
                .put("created", dispatchMsgDTO.getCreated()).build()
        );

        // 8. 将构运单id存到 set集合中，用于后续幂等性判断
        //存储到redis
        this.stringRedisTemplate.opsForList().leftPush(listRedisKey, value);
        this.stringRedisTemplate.opsForSet().add(setRedisKey, transportOrderId);
    }

    public String getListRedisKey(Long startId, Long endId) {
        return StrUtil.format("DISPATCH_LIST_{}_{}", startId, endId);
    }

    public String getSetRedisKey(Long startId, Long endId) {
        return StrUtil.format("DISPATCH_SET_{}_{}", startId, endId);
    }

}
