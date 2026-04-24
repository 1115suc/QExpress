package course.QExpress.ms.base.controller.base;

import course.QExpress.common.util.PageResponse;
import course.QExpress.ms.base.domain.base.WorkPatternAddDTO;
import course.QExpress.ms.base.domain.base.WorkPatternDTO;
import course.QExpress.ms.base.domain.base.WorkPatternQueryDTO;
import course.QExpress.ms.base.domain.base.WorkPatternUpdateDTO;
import course.QExpress.ms.base.service.base.WorkPatternService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("work-patterns")
@RestController
@Slf4j
@Api(tags = "工作模式相关接口")
public class WorkPatternController {

    @Resource
    private WorkPatternService workPatternService;

    @GetMapping("all")
    @ApiOperation("工作模式列表查询")
    public List<WorkPatternDTO> all() {
       return workPatternService.all();
    }

    @GetMapping("page")
    @ApiOperation("工作模式列表查询")
    public PageResponse<WorkPatternDTO> list(WorkPatternQueryDTO workPatternQueryDTO) {
        log.info("workPatternQueryDTO : {}", workPatternQueryDTO);
        return workPatternService.page(workPatternQueryDTO);
    }

    @GetMapping("{id}")
    @ApiOperation("根据工作模式id获取工作模式详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工作模式id")
    })
    public ResponseEntity<WorkPatternDTO> getById(@PathVariable("id") Long id) {
        WorkPatternDTO workPatternDTO = workPatternService.findById(id);
        return ResponseEntity.ok(workPatternDTO);
    }

    @DeleteMapping("{id}")
    @ApiOperation("工作模式删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工作模式id")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        workPatternService.delete(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping
    @ApiOperation("新增工作模式")
    public ResponseEntity<Void> add(@RequestBody WorkPatternAddDTO workPatternAddDTO) {
        workPatternService.add(workPatternAddDTO);
        return ResponseEntity.ok(null);
    }
    @PutMapping("")
    @ApiOperation("修改工作模式")
    public ResponseEntity<Void> put(@RequestBody WorkPatternUpdateDTO workPatternUpdateDTO){
        workPatternService.update(workPatternUpdateDTO);
        return ResponseEntity.ok(null);
    }

}
