package course.QExpress.transport.info.mq;

import cn.hutool.core.convert.Convert;
import com.github.benmanes.caffeine.cache.Cache;
import course.QExpress.transport.info.domain.dto.TransportInfoDTO;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * redis消息监听，解决Caffeine一致性的问题
 */
@Component
public class RedisMessageListener extends MessageListenerAdapter {
    @Resource
    private Cache<String, TransportInfoDTO> transportInfoCache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        //获取到消息中的运单id
        String transportOrderId = Convert.toStr(message);
        //将本jvm中的缓存删除掉
        this.transportInfoCache.invalidate(transportOrderId);
    }
}