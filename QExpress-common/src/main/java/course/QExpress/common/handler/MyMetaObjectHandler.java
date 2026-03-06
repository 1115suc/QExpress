package course.QExpress.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元对象处理器
 * 实现了 MyBatis-Plus 的 MetaObjectHandler 接口，用于自动填充实体类的公共字段
 * 如创建时间（created）和更新时间（updated）
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时的字段填充方法
     * 在实体对象插入数据库时自动填充 created 和 updated 字段为当前时间
     *
     * @param metaObject MyBatis 的元对象，封装了待插入的实体对象信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Object created = getFieldValByName("created", metaObject);
        if (null == created) {
            //字段为空，可以进行填充
            setFieldValByName("created", LocalDateTime.now(), metaObject);
        }

        Object updated = getFieldValByName("updated", metaObject);
        if (null == updated) {
            //字段为空，可以进行填充
            setFieldValByName("updated", LocalDateTime.now(), metaObject);
        }
    }

    /**
     * 更新操作时的字段填充方法
     * 在实体对象更新数据库时自动更新 updated 字段为当前时间
     *
     * @param metaObject MyBatis 的元对象，封装了待更新的实体对象信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //更新数据时，直接更新字段
        setFieldValByName("updated", LocalDateTime.now(), metaObject);
    }
}
