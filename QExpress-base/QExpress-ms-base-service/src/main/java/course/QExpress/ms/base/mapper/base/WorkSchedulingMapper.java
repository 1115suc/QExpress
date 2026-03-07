package course.QExpress.ms.base.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import course.QExpress.ms.base.entity.base.WorkSchedulingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkSchedulingMapper extends BaseMapper<WorkSchedulingEntity> {

    void batchInsert(@Param("entities") List<WorkSchedulingEntity> workSchedulingEntities);
}
