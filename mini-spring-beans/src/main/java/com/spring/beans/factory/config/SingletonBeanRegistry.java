package com.spring.beans.factory.config;

/**
 * ClassName: SingletonBeanRegistry
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 15:36
 * @version: v1.0
 */
public interface SingletonBeanRegistry {


    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    /**
     * 检查是否包含单例Bean
     */
    boolean containsSingleton(String beanName);

    /**
     * 获取所有单例Bean的名称
     */
    String[] getSingletonNames();
}
