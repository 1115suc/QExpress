package course.QExpress.carriage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.carriage.entity.CarriageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运费管理表 mapper接口
 */
@Mapper
public interface CarriageMapper extends BaseMapper<CarriageEntity> {}
