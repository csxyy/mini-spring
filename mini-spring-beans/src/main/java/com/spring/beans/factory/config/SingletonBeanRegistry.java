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
}
