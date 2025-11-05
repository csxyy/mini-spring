package com.spring.beans.factory;

/**
 * ClassName: BeanFactory
 * Description: BeanFactory 基础接口 - 定义获取Bean的基本能力
 *
 * @Author: csx
 * @Create: 2025/10/28 - 9:12
 * @version: v1.0
 */
public interface BeanFactory {
    /**
     * 根据名称获取Bean实例
     * @param name Bean名称
     * @return Bean实例
     */
    Object getBean(String name);

    /**
     * 根据类型获取Bean实例
     * @param requiredType Bean类型
     * @return Bean实例
     */
    <T> T getBean(Class<T> requiredType);

    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 检查是否包含指定名称的Bean
     * @param name Bean名称
     * @return 是否包含
     */
    boolean containsBean(String name);
}
