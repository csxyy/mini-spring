package com.spring.beans.factory;

/**
 * ClassName: ObjectFactory
 * Description: 对象工厂接口 - 用于延迟创建对象
 *
 * @Author: csx
 * @Create: 2025/11/5 - 23:42
 * @version: v1.0
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    /**
     * 获取对象实例
     */
    T getObject();

}
