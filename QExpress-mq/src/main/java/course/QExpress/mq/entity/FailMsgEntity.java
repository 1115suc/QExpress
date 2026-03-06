package course.QExpress.mq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import course.QExpress.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 失败消息记录表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("qe_fail_msg")
public class FailMsgEntity extends BaseEntity {

    private String msgId; //消息id
    private String exchange; //交换机
    private String routingKey; //路由key
    private String msg; //消息内容
    private String reason; //失败原因

}