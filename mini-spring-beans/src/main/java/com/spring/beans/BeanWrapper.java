package com.spring.beans;

import java.beans.PropertyDescriptor;

/**
 * ClassName: BeanWrapper
 * Description: Bean包装器接口 - 用于统一操作Bean属性 - 只实现核心方法
 *
 * @Author: csx
 * @Create: 2025/11/10 - 11:45
 * @version: v1.0
 */
public interface BeanWrapper {
    /**
     * 获取包装的Bean实例
     */
    Object getWrappedInstance();

    /**
     * 获取Bean的类型
     */
    Class<?> getWrappedClass();

    /**
     * 设置属性值
     * @param propertyName 属性名
     * @param value 属性值
     */
    void setPropertyValue(String propertyName, Object value);

    /**
     * 获取属性值
     * @param propertyName 属性名
     * @return 属性值
     */
    Object getPropertyValue(String propertyName);

    /**
     * 获取属性类型
     * @param propertyName 属性名
     * @return 属性类型
     */
    Class<?> getPropertyType(String propertyName);

    /**
     * 是否可写属性
     * @param propertyName 属性名
     * @return 是否可写
     */
    boolean isWritableProperty(String propertyName);

    /**
     * 是否可读属性
     * @param propertyName 属性名
     * @return 是否可读
     */
    boolean isReadableProperty(String propertyName);
}
