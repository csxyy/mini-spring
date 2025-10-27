package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanFactoryPostProcessor;

/**
 * ClassName: BeanDefinitionRegistryPostProcessor
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:01
 * @version: v1.0
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    /**
     * 注册BeanDefinition核心方法
     * @param registry
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);
}
