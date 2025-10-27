package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;

/**
 * ClassName: BeanDefinitionRegistry
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:51
 * @version: v1.0
 */
public interface BeanDefinitionRegistry {

    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 检查是否包含beanName
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);


    /**
     * 注册BeanDefinition
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
