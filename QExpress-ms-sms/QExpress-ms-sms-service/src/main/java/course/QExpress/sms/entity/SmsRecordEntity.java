package course.QExpress.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import course.QExpress.common.entity.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sms_record")
public class SmsRecordEntity extends BaseEntity {

    private String mobile;

    private Long userId;

    private String thirdChannelCode;

    private String verifyCode;

    private Integer bussinessType;

    private Integer smsType;

    private Integer contentType;

    private Long batchId;

    private Long creater;

    private Long updater;

}