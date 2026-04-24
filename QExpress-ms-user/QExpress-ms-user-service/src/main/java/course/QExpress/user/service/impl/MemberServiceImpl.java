package course.QExpress.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.user.entity.MemberEntity;
import course.QExpress.user.mapper.MemberMapper;
import course.QExpress.user.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * 用户表  服务类实现
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, MemberEntity>
        implements MemberService {

}
