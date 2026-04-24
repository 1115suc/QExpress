package course.QExpress.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import course.QExpress.user.entity.AddressBookEntity;
import course.QExpress.user.mapper.AddressBookMapper;
import course.QExpress.user.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户表  服务类实现
 */
@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBookEntity>
        implements AddressBookService {

}
