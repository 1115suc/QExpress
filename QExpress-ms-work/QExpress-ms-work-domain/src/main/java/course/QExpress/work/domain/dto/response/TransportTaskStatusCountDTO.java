package course.QExpress.work.domain.dto.response;

import course.QExpress.work.domain.enums.transporttask.TransportTaskStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportTaskStatusCountDTO {

    @ApiModelProperty(value = "状态枚举", required = true)
    private TransportTaskStatus status;
    @ApiModelProperty(value = "状态编码", required = true)
    private Integer statusCode;
    @ApiModelProperty(value = "数量", required = true)
    private Long count;

}
