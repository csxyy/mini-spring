package com.spring.beans.factory.config;

import com.spring.beans.factory.HierarchicalBeanFactory;
import com.spring.beans.factory.support.BeanPostProcessor;

/**
 * ClassName: ConfigurableBeanFactory
 * Description: 可配置的BeanFactory - 提供了配置BeanFactory的能力
 *
 * @Author: csx
 * @Create: 2025/10/28 - 9:13
 * @version: v1.0
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    /**
     * 设置Bean类加载器
     * @param beanClassLoader 类加载器
     */
    void setBeanClassLoader(ClassLoader beanClassLoader);

    /**
     * 获取Bean类加载器
     * @return 类加载器
     */
    ClassLoader getBeanClassLoader();

    /**
     * 添加后置处理器
     * @param beanPostProcessor 后置处理器
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 判断是否为FactoryBean
     * @param name
     * @return
     */
    boolean isFactoryBean(String name);
}
