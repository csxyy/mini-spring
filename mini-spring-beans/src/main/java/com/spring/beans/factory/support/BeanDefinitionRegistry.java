package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;

/**
 * ClassName: BeanDefinitionRegistry
 * Description: Bean定义注册表 - 用于注册和管理Bean定义
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:51
 * @version: v1.0
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册BeanDefinition
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 移除Bean定义
     * @param beanName Bean名称
     */
    void removeBeanDefinition(String beanName);

    /**
     * 获取Bean定义
     * @param beanName Bean名称
     * @return Bean定义
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 检查是否包含Bean定义
     * @param beanName Bean名称
     * @return 是否包含
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 获取所有Bean定义名称
     * @return Bean名称数组
     */
    String[] getBeanDefinitionNames();

    /**
     * 获取Bean定义数量
     * @return Bean定义数量
     */
    int getBeanDefinitionCount();

}
