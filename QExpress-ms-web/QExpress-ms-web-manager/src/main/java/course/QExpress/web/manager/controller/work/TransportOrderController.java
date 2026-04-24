package course.QExpress.web.manager.controller.work;

import course.QExpress.common.util.PageResponse;
import course.QExpress.common.vo.R;
import course.QExpress.web.manager.service.WorkService;
import course.QExpress.web.manager.vo.work.TrackVO;
import course.QExpress.web.manager.vo.work.TransportOrderQueryVO;
import course.QExpress.web.manager.vo.work.TransportOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 运单管理
 */
@Slf4j
@Api(tags = "运单相关")
@RestController
@RequestMapping("transport-order-manager")
public class TransportOrderController {

    @Resource
    private WorkService workService;

    @ApiOperation(value = "获取运单分页数据")
    @PostMapping("/page")
    public R<PageResponse<TransportOrderVO>> findByPage(@RequestBody TransportOrderQueryVO vo) {
        PageResponse<TransportOrderVO> byPage = workService.findTransportOrderByPage(vo);
        return R.success(byPage);
    }

    @ApiOperation(value = "获取运单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "运单id", required = true, example = "1")
    })
    @GetMapping("/{id}")
    public R<TransportOrderVO> findById(@PathVariable(name = "id") String id) {
        TransportOrderVO byId = workService.findTransportOrderDetail(id);
        return R.success(byId);
    }

    @ApiOperation(value = "统计运单")
    @GetMapping("/count")
    public R<Map<Integer, Long>> count() {
        Map<Integer, Long> count = workService.countTransportOrder();
        return R.success(count);
    }

    @ApiOperation(value = "获取运单轨迹详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "运单id", required = true, example = "1")
    })
    @GetMapping("/track/{id}")
    public R<TrackVO> findTrackById(@PathVariable(name = "id") String id) {
        TrackVO track = workService.findTrackById(id);
        return R.success(track);
    }
}
