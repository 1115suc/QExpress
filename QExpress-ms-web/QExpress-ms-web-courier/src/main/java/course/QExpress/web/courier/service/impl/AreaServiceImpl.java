package course.QExpress.web.courier.service.impl;

import cn.hutool.core.collection.CollUtil;
import course.QExpress.common.util.ObjectUtils;
import course.QExpress.ms.base.api.common.AreaFeign;
import course.QExpress.ms.base.domain.base.AreaDto;
import course.QExpress.web.courier.service.AreaService;
import course.QExpress.web.courier.vo.area.AreaSimpleVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {

    @Resource
    private AreaFeign areaFeign;

    @Override
    public List<AreaSimpleVO> findChildrenAreaByParentId(Long parentId) {

        List<AreaDto> areas = areaFeign.findAll(parentId, null);
        if (CollUtil.isEmpty(areas)) {
            return Collections.emptyList();
        }
        List<AreaSimpleVO> areaSimpleVOS = new ArrayList<>();
        areas.forEach(area -> {
            AreaSimpleVO areaSimpleVO = ObjectUtils.convert(area, AreaSimpleVO.class);
            areaSimpleVOS.add(areaSimpleVO);
        });
        return areaSimpleVOS;
    }
}
