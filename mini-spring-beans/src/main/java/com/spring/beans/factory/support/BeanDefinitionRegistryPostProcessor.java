package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * ClassName: BeanDefinitionRegistryPostProcessor
 * Description: BeanDefinition注册表后置处理器接口
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:01
 * @version: v1.0
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    /**
     * 注册BeanDefinition核心方法
     * 在标准初始化之后修改应用程序上下文的内部Bean定义注册表
     * @param registry
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry);

    @Override
    default void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }
}
