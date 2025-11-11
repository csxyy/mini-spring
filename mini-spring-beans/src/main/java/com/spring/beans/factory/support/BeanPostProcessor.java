package com.spring.beans.factory.support;

/**
 * ClassName: BeanPostProcessor
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:29
 * @version: v1.0
 */
public interface  BeanPostProcessor {

    /**
     * 初始化前
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * 初始化后
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
