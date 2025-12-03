package com.spring.beans.factory.config;

import com.spring.beans.factory.ListableBeanFactory;

/**
 * ClassName: ConfigurableListableBeanFactory
 * Description:
 *
 *  可配置的Listable BeanFactory - 组合了配置和枚举能力
 *  这是Spring中BeanFactory体系的顶级配置接口
 *
 * @Author: csx
 * @Create: 2025/10/28 - 8:50
 * @version: v1.0
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    /**
     * 设置序列化ID（用于序列化/反序列化）
     * @param serializationId 序列化ID
     */
    void setSerializationId(String serializationId);

    /**
     * 冻结BeanFactory配置（防止运行时修改）
     */
    void freezeConfiguration();

    /**
     * 检查配置是否已冻结
     * @return 是否已冻结
     */
    boolean isConfigurationFrozen();

    /**
     * 忽略给定的自动装配依赖接口
     * @param type 要忽略的依赖类型
     */
    void ignoreDependencyInterface(Class<?> type);

    /**
     *
     * @param dependencyType
     * @param autowiredValue
     */
    void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue);

    /**
     * 实例化非懒加载的单例Bean
     */
    void preInstantiateSingletons();

    BeanDefinition getBeanDefinition(String beanName);

}
