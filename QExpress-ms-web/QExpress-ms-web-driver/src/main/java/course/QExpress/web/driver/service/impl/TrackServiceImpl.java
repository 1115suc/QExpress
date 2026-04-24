package course.QExpress.web.driver.service.impl;

import cn.hutool.core.collection.CollUtil;
import course.QExpress.common.util.PageResponse;
import course.QExpress.common.util.UserThreadLocal;
import course.QExpress.diver.api.DriverJobFeign;
import course.QExpress.driver.domain.dto.request.DriverJobPageQueryDTO;
import course.QExpress.driver.domain.dto.response.DriverJobDTO;
import course.QExpress.driver.domain.enums.DriverJobStatus;
import course.QExpress.track.api.TrackFeign;
import course.QExpress.web.driver.service.TrackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrackServiceImpl implements TrackService {
    @Resource
    private DriverJobFeign driverJobFeign;
    @Resource
    private TrackFeign trackFeign;

    /**
     * 车辆上报位置
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 是否成功
     */
    @Override
    public Boolean uploadLocation(String lng, String lat) {
        //1. 获取当前用户id
        Long userId = UserThreadLocal.getUserId();

        //2. 查询司机id关联的在途状态司机作业单
        DriverJobPageQueryDTO pageQueryDTO = DriverJobPageQueryDTO.builder()
                .page(1)
                .pageSize(1)
                .driverId(userId)
                .statusList(List.of(DriverJobStatus.PROCESSING))
                .build();
        PageResponse<DriverJobDTO> pageResponse = driverJobFeign.pageQuery(pageQueryDTO);
        if (CollUtil.isEmpty(pageResponse.getItems())) {
            return true;
        }

        //3. 对关联的运输任务上报位置
        return trackFeign.uploadFromTruck(pageResponse.getItems().get(0).getTransportTaskId(), Double.parseDouble(lng), Double.parseDouble(lat));
    }
}
