package com.spring.beans.factory.config;

/**
 * ClassName: BeanFactoryPostProcessor
 * Description:
 *
 * BeanFactory后置处理器接口，允许自定义修改应用程序上下文的 bean 定义，调整上下文底层 bean 工厂的 bean 属性值。
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:00
 * @version: v1.0
 */
public interface BeanFactoryPostProcessor {

    /**
     * 在BeanFactory标准初始化之后修改应用程序上下文的内部Bean工厂
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
}
