package course.QExpress.ms.base.utils;

import course.QExpress.ms.base.constant.WorkConstants;
import course.QExpress.ms.base.domain.enums.WorkPatternEnum;
import course.QExpress.ms.base.entity.base.WorkPatternEntity;

public class WorkPatternUtils {

    public static String toWorkDate(WorkPatternEntity entity) {
        byte workPatternType = entity.getWorkPatternType();
        if (workPatternType == WorkPatternEnum.Weeks.getType()) { //周期制
            String workDate = String.format(WorkConstants.WORK_DATE_WEEKS, entity.getMonday(), entity.getTuesday(),
                    entity.getWednesday(), entity.getThursday(), entity.getFriday(),
                    entity.getSaturday(), entity.getSunday());
            return workDate.replace("1", "上").replace("2", "休");
        } else {
            return String.format(WorkConstants.WORK_DATE_CONTINUITYS,
                    entity.getWorkDayNum(), entity.getRestDayNum());
        }
    }
}
