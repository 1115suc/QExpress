package course.QExpress.web.courier.service;

import course.QExpress.web.courier.vo.area.AreaSimpleVO;

import java.util.List;

public interface AreaService {
    List<AreaSimpleVO> findChildrenAreaByParentId(Long parentId);
}
