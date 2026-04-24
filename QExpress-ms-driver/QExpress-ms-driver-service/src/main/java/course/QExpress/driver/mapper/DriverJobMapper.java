package course.QExpress.driver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.driver.entity.DriverJobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 司机作业单 Mapper 接口
 */
@Mapper
public interface DriverJobMapper extends BaseMapper<DriverJobEntity> {
}
