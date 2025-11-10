package com.spring.beans.factory;

/**
 * ClassName: FactoryBean
 * Description: FactoryBean接口 - 用于创建复杂对象的工厂Bean
 *
 * @Author: csx
 * @Create: 2025/11/10 - 9:38
 * @version: v1.0
 */
public interface FactoryBean<T> {
    /**
     * 返回由FactoryBean创建的对象
     */
    T getObject();

    /**
     * 返回FactoryBean创建的对象类型
     */
    Class<?> getObjectType();

    /**
     * 返回是否是单例
     */
    default boolean isSingleton() {
        return true;
    }
}
