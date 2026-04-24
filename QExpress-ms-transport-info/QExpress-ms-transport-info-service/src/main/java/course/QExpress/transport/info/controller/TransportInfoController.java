package course.QExpress.transport.info.controller;

import com.github.benmanes.caffeine.cache.Cache;
import course.QExpress.common.exception.QEException;
import course.QExpress.common.util.BeanUtil;
import course.QExpress.common.util.ObjectUtil;
import course.QExpress.transport.info.domain.dto.TransportInfoDTO;
import course.QExpress.transport.info.entity.TransportInfoEntity;
import course.QExpress.transport.info.enums.ExceptionEnum;
import course.QExpress.transport.info.service.TransportInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "物流信息")
@RestController
@RequestMapping("infos")
public class TransportInfoController {

    @Resource
    private TransportInfoService transportInfoService;

    @Resource
    private Cache<String, TransportInfoDTO> transportInfoCache;

    /**
     * 根据运单id查询运单信息
     *
     * @param transportOrderId 运单号
     * @return 运单信息
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transportOrderId", value = "运单id")
    })
    @ApiOperation(value = "查询", notes = "根据运单id查询物流信息")
    @GetMapping("{transportOrderId}")
    public TransportInfoDTO queryByTransportOrderId(@PathVariable("transportOrderId") String transportOrderId) {

        // TODO day10 基于布隆过滤器，提前拒绝一定不存在的数据查询，解决缓存穿透问题

        // 相关配置 自行拷贝


        // TODO day10 基于caffeine实现一级缓存优化
        TransportInfoDTO transportInfoDTO = this.transportInfoCache.get(transportOrderId, s -> {
            TransportInfoEntity transportInfoEntity = this.transportInfoService.queryByTransportOrderId(transportOrderId);
            return BeanUtil.toBean(transportInfoEntity, TransportInfoDTO.class);
        });

        if (ObjectUtil.isNotEmpty(transportInfoDTO)) {
            return transportInfoDTO;
        }
        throw new QEException(ExceptionEnum.NOT_FOUND);
    }

}
