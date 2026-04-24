package course.QExpress.transport.service.impl;

import course.QExpress.transport.entity.node.OLTEntity;
import course.QExpress.transport.repository.OLTRepository;
import course.QExpress.transport.service.OLTService;
import org.springframework.stereotype.Service;

@Service
public class OLTServiceImpl extends ServiceImpl<OLTRepository, OLTEntity>
        implements OLTService {
}
