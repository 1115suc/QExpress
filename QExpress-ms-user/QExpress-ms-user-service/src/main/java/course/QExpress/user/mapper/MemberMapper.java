package course.QExpress.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.user.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表  Mapper 接口
 */
@Mapper
public interface MemberMapper extends BaseMapper<MemberEntity> {

}
