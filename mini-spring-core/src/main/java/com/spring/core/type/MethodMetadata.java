package com.spring.core.type;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ClassName: MethodMetadata
 * Description: 方法元数据接口
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:05
 * @version: v1.0
 */
public interface MethodMetadata {
    /**
     * 获取方法名
     */
    String getMethodName();


    /**
     * 获取声明类名
     */
    String getDeclaringClassName();

    /**
     * 检查方法是否包含指定注解
     */
    boolean isAnnotated(String annotationName);

    /**
     * 获取方法上的注解属性
     */
    Map<String, Object> getAnnotationAttributes(String annotationName);

    String getReturnTypeName();

    boolean isAbstract();

    boolean isStatic();

    boolean isFinal();

    Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString);
}
