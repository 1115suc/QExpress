package course.QExpress.web.manager.controller.work;

import course.QExpress.common.vo.R;
import course.QExpress.web.manager.service.WorkService;
import course.QExpress.web.manager.vo.work.DispatchConfigurationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 调度配置管理
 */
@Api(tags = "调度配置")
@Slf4j
@RestController
@RequestMapping("dispatch-configuration-manager")
public class DispatchConfigurationController {
    @Resource
    private WorkService workService;

    @ApiOperation(value = "保存调度配置")
    @PostMapping()
    public R<Void> saveConfiguration(@Valid @RequestBody DispatchConfigurationVO vo) {
        workService.saveConfiguration(vo);
        return R.success();
    }

    @ApiOperation(value = "查询调度配置")
    @GetMapping()
    public R<DispatchConfigurationVO> findConfiguration() {
        return R.success(workService.findConfiguration());
    }
}
