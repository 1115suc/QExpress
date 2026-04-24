package course.QExpress.transport.service.impl;

import course.QExpress.transport.entity.node.TLTEntity;
import course.QExpress.transport.repository.TLTRepository;
import course.QExpress.transport.service.TLTService;
import org.springframework.stereotype.Service;

@Service
public class TLTServiceImpl extends ServiceImpl<TLTRepository, TLTEntity>
        implements TLTService {

}
