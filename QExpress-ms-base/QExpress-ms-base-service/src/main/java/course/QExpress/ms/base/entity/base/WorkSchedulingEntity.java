package course.QExpress.ms.base.entity.base;

import com.baomidou.mybatisplus.annotation.TableName;
import course.QExpress.common.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("qe_work_scheduling")
@Data
public class WorkSchedulingEntity extends BaseEntity {
    private Long userId;

    private Byte userType;

    private Long agencyId;

    private String employeeNumber;

    private String name;

    private String phone;

    private Byte state;

    private Long workPatternId;

    private LocalDateTime workContinueStartTime;

    private Long creater;

    private Long updater;

    private Boolean isDelete;

}