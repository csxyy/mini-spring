package com.spring.core.type;

import java.util.Map;
import java.util.Set;

/**
 * ClassName: AnnotationMetadata
 * Description: 注解元数据接口
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:04
 * @version: v1.0
 */
public interface AnnotationMetadata {
    /**
     * 获取类名
     */
    String getClassName();


    /**
     * 检查注解的继承链（包括元注解）
     */
    boolean isAnnotated(String annotationName);

    /**
     * 获取指定注解的属性值
     * 对应Spring的getAnnotationAttributes方法
     *
     * @param annotationName 注解全限定名
     * @return 注解属性映射，如果不存在返回null
     */
    Map<String, Object> getAnnotationAttributes(String annotationName);

    /**
     * 获取指定注解的属性值（包括默认值）
     *
     * @param annotationName 注解全限定名
     * @param classValuesAsString 是否将Class值转换为字符串
     * @return 注解属性映射
     */
    Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString);

    /**
     * 获取所有注解类型名称
     */
    Set<String> getAnnotationTypes();

    /**
     * 检查是否是接口
     */
    boolean isInterface();

    /**
     * 检查是否是抽象类
     */
    boolean isAbstract();
}
