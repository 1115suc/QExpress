package course.QExpress.transport.service;

import course.QExpress.transport.domain.DispatchConfigurationDTO;

/**
 * 调度配置相关业务
 */
public interface DispatchConfigurationService {
    /**
     * 查询调度配置
     *
     * @return 调度配置
     */
    DispatchConfigurationDTO findConfiguration();

    /**
     * 保存调度配置
     * @param dto 调度配置
     */
    void saveConfiguration(DispatchConfigurationDTO dto);
}
