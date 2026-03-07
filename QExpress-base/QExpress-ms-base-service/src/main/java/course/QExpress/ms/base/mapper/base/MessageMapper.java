package course.QExpress.ms.base.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.ms.base.entity.base.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息表 mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {

}
