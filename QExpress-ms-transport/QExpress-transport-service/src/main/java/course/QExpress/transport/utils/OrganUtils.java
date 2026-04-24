package course.QExpress.transport.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import course.QExpress.common.exception.QEException;
import course.QExpress.transport.domain.OrganDTO;
import course.QExpress.transport.entity.node.AgencyEntity;
import course.QExpress.transport.entity.node.BaseEntity;
import course.QExpress.transport.entity.node.OLTEntity;
import course.QExpress.transport.entity.node.TLTEntity;
import course.QExpress.transport.enums.ExceptionEnum;
import course.QExpress.transport.enums.OrganTypeEnum;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构工具类
 */
public class OrganUtils {

    public static OrganDTO toAgencyDTO(BaseEntity baseEntity) {
        OrganDTO organDTO = BeanUtil.toBean(baseEntity, OrganDTO.class, CopyOptions.create().setIgnoreProperties("id", "location"));
        //数据库中的bid是对外的id
        organDTO.setId(baseEntity.getBid());
        organDTO.setLatitude(BeanUtil.getProperty(baseEntity.getLocation(), "x"));
        organDTO.setLongitude(BeanUtil.getProperty(baseEntity.getLocation(), "y"));
        organDTO.setType(baseEntity.getAgencyType().getCode());
        return organDTO;
    }

    public static BaseEntity toEntity(OrganDTO organDTO) {
        BaseEntity baseEntity;
        OrganTypeEnum organType = OrganTypeEnum.codeOf(organDTO.getType());
        switch (organType) {
            case OLT: {
                baseEntity = BeanUtil.toBean(organDTO, OLTEntity.class, CopyOptions.create().ignoreNullValue());
                break;
            }
            case TLT: {
                baseEntity = BeanUtil.toBean(organDTO, TLTEntity.class, CopyOptions.create().ignoreNullValue());
                break;
            }
            case AGENCY: {
                baseEntity = BeanUtil.toBean(organDTO, AgencyEntity.class, CopyOptions.create().ignoreNullValue());
                break;
            }
            default: {
                throw new QEException(ExceptionEnum.ORGAN_TYPE_ERROR);
            }
        }

        baseEntity.setId(null);
        baseEntity.setBid(organDTO.getId());
        if (ObjectUtil.isAllNotEmpty(organDTO.getLatitude(), organDTO.getLongitude())) {
            baseEntity.setLocation(new Point(organDTO.getLatitude(), organDTO.getLongitude()));
        }
        return baseEntity;
    }

    public static List<OrganDTO> toAgencyDTOList(List<? extends BaseEntity> list) {
        return list.stream()
                .map(OrganUtils::toAgencyDTO)
                .collect(Collectors.toList());
    }

}
