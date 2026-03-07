package course.QExpress.ms.base.controller.truck;

import course.QExpress.common.util.BeanUtil;
import course.QExpress.common.util.PageResponse;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterDTO;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterListDTO;
import course.QExpress.ms.base.domain.truck.TruckReturnRegisterPageQueryDTO;
import course.QExpress.ms.base.entity.truck.TruckReturnRegisterEntity;
import course.QExpress.ms.base.service.truck.TruckReturnRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 回车登记
 */
@Api(tags = "回车登记")
@RestController
@RequestMapping("base/returnRegister")
@Slf4j
public class TruckReturnRegisterController {
    @Resource
    private TruckReturnRegisterService truckReturnRegisterService;

    @PostMapping
    @ApiOperation(value = "新增回车登记", notes = "新增回车登记记录")
    public void save(@RequestBody TruckReturnRegisterDTO truckReturnRegisterDTO) {
        TruckReturnRegisterEntity truckReturnRegisterEntity = BeanUtil.toBean(truckReturnRegisterDTO, TruckReturnRegisterEntity.class);
        truckReturnRegisterService.save(truckReturnRegisterEntity);
    }

    @PostMapping("pageQuery")
    @ApiOperation(value = "分页查询回车登记列表")
    public PageResponse<TruckReturnRegisterListDTO> pageQuery(@RequestBody TruckReturnRegisterPageQueryDTO truckReturnRegisterPageQueryDTO) {
        return truckReturnRegisterService.pageQuery(truckReturnRegisterPageQueryDTO);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询回车登记详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "回车登记id",dataTypeClass = Long.class)})
    public TruckReturnRegisterDTO findById(@PathVariable("id") Long id) {
        return truckReturnRegisterService.findById(id);
    }
}