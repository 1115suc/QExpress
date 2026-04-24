package course.QExpress.transport.utils;


import cn.hutool.extra.spring.SpringUtil;
import course.QExpress.common.exception.QEException;
import course.QExpress.transport.enums.ExceptionEnum;
import course.QExpress.transport.enums.OrganTypeEnum;
import course.QExpress.transport.service.AgencyService;
import course.QExpress.transport.service.IService;
import course.QExpress.transport.service.OLTService;
import course.QExpress.transport.service.TLTService;

/**
 * 根据type选择对应的service返回
 */
public class OrganServiceFactory {

    public static IService getBean(Integer type) {
        OrganTypeEnum organTypeEnum = OrganTypeEnum.codeOf(type);
        if (null == organTypeEnum) {
            throw new QEException(ExceptionEnum.ORGAN_TYPE_ERROR);
        }

        switch (organTypeEnum) {
            case AGENCY: {
                return SpringUtil.getBean(AgencyService.class);
            }
            case OLT: {
                return SpringUtil.getBean(OLTService.class);
            }
            case TLT: {
                return SpringUtil.getBean(TLTService.class);
            }
        }
        return null;
    }

}
