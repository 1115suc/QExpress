package course.QExpress.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.user.entity.AddressBookEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址簿  Mapper 接口
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBookEntity> {

}
