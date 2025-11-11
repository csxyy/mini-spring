package com.spring.beans;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: BeanWrapperImpl
 * Description: Bean包装器实现类
 *
 * @Author: csx
 * @Create: 2025/11/11 - 22:43
 * @version: v1.0
 */
@Slf4j
public class BeanWrapperImpl implements BeanWrapper {
    private final Object wrappedObject;

    public BeanWrapperImpl(Object object) {
        this.wrappedObject = object;
    }

    @Override
    public Object getWrappedInstance() {
        return this.wrappedObject;
    }

    @Override
    public Class<?> getWrappedClass() {
        return this.wrappedObject.getClass();
    }
    @Override
    public void setPropertyValue(String propertyName, Object value) {
        log.debug("设置属性: {}.{} = {}",
                wrappedObject.getClass().getSimpleName(), propertyName, value);

        try {
            // 通过反射找到setter方法
            Method setter = findSetterMethod(propertyName);
            if (setter == null) {
                throw new IllegalArgumentException("找不到setter方法: " + propertyName);
            }

            // 调用setter方法
            setter.invoke(wrappedObject, value);

        } catch (Exception e) {
            throw new RuntimeException("设置属性失败: " + propertyName, e);
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        log.debug("获取属性: {}.{}",
                wrappedObject.getClass().getSimpleName(), propertyName);

        try {
            // 通过反射找到getter方法
            Method getter = findGetterMethod(propertyName);
            if (getter == null) {
                throw new IllegalArgumentException("找不到getter方法: " + propertyName);
            }

            // 调用getter方法
            return getter.invoke(wrappedObject);

        } catch (Exception e) {
            throw new RuntimeException("获取属性失败: " + propertyName, e);
        }
    }

    @Override
    public Class<?> getPropertyType(String propertyName) {
        Method getter = findGetterMethod(propertyName);
        if (getter != null) {
            return getter.getReturnType();
        }

        Method setter = findSetterMethod(propertyName);
        if (setter != null) {
            return setter.getParameterTypes()[0];
        }

        throw new IllegalArgumentException("找不到属性: " + propertyName);
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return findSetterMethod(propertyName) != null;
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return findGetterMethod(propertyName) != null;
    }

    // ============ 私有工具方法 ============

    /**
     * 查找setter方法
     */
    private Method findSetterMethod(String propertyName) {
        String setterName = "set" + capitalize(propertyName);
        Class<?> clazz = wrappedObject.getClass();

        // 遍历所有方法查找setter
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterName) &&
                    method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    /**
     * 查找getter方法
     */
    private Method findGetterMethod(String propertyName) {
        String getterName = "get" + capitalize(propertyName);
        String booleanGetterName = "is" + capitalize(propertyName);
        Class<?> clazz = wrappedObject.getClass();

        // 遍历所有方法查找getter
        for (Method method : clazz.getMethods()) {
            if ((method.getName().equals(getterName) ||
                    method.getName().equals(booleanGetterName)) &&
                    method.getParameterCount() == 0) {
                return method;
            }
        }
        return null;
    }

    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
