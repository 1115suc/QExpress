package course.QExpress.ms.base.entity.base;

import com.baomidou.mybatisplus.annotation.TableName;
import course.QExpress.common.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("qe_work_history_scheduling")
public class WorkHistorySchedulingEntity extends BaseEntity {
    private Long userId;

    private String name;

    private String phone;

    private String employeeNumber;

    private Integer workDay;

    private String workMonth;

    private Byte userType;

    private Byte workPatternType;

    private Long workPatternId;
}