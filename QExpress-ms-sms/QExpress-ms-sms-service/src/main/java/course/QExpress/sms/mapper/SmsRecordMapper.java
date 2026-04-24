package course.QExpress.sms.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.sms.entity.SmsRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 短信发送记录
 * </p>
 *
 */
@Mapper
@TableName("sms_record")
public interface SmsRecordMapper extends BaseMapper<SmsRecordEntity> {

    int batchInsert(@Param("entities") List<SmsRecordEntity> entities);
}
