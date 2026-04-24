package course.QExpress.transport.controller;

import course.QExpress.transport.domain.DispatchConfigurationDTO;
import course.QExpress.transport.service.DispatchConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "调度配置")
@RequestMapping("dispatch-configuration")
@Validated
@RestController
public class DispatchConfigurationController {
    @Resource
    private DispatchConfigurationService dispatchConfigurationService;

    @ApiOperation(value = "查询调度配置")
    @GetMapping
    public DispatchConfigurationDTO findConfiguration() {
        return dispatchConfigurationService.findConfiguration();
    }

    @ApiOperation(value = "保存调度配置")
    @PostMapping
    public void saveConfiguration(@RequestBody DispatchConfigurationDTO dto) {
        dispatchConfigurationService.saveConfiguration(dto);
    }
}
