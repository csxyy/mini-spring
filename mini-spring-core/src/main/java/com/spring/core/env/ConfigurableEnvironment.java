package com.spring.core.env;

/**
 * ClassName: ConfigurableEnvironment
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/27 - 11:56
 * @version: v1.0
 */
public interface ConfigurableEnvironment extends Environment {
    /**
     * 设置激活的profiles
     * @param requiredProperties
     */
    void setRequiredProperties(String... requiredProperties);

    /**
     * 添加属性源（如：系统属性、环境变量、配置文件等）
     * @param propertySource
     */
    void addPropertySource(PropertySource propertySource);

    /**
     * 验证必需属性
     */
    void validateRequiredProperties();

}
