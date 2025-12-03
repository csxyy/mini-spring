package com.spring.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: StandardMethodMetadata
 * Description: 标准方法元数据实现
 *
 * 作用：封装方法的注解信息，特别是@Bean方法的元数据
 * 设计思想：与方法相关的注解查询都通过这个接口，隐藏反射细节
 *
 * 为什么需要：@Bean方法可能有各种注解配置（@Lazy、@Scope等），
 * 需要统一的方式来访问这些配置信息
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:23
 * @version: v1.0
 */

public class StandardMethodMetadata implements MethodMetadata {
    /** 被扫描的方法对象 */
    private final Method introspectedMethod;

    /** 方法注解缓存 */
    private final Map<String, Map<String, Object>> annotationAttributes;

    public StandardMethodMetadata(Method introspectedMethod) {
        this.introspectedMethod = introspectedMethod;
        this.annotationAttributes = new HashMap<>();
        scanMethodAnnotations();
    }

    private void scanMethodAnnotations() {
        for (Annotation annotation : introspectedMethod.getDeclaredAnnotations()) {
            String annotationType = annotation.annotationType().getName();
            Map<String, Object> attributes = extractAnnotationAttributes(annotation);
            annotationAttributes.put(annotationType, attributes);
        }
    }

    private Map<String, Object> extractAnnotationAttributes(Annotation annotation) {
        Map<String, Object> attributes = new HashMap<>();
        Method[] methods = annotation.annotationType().getDeclaredMethods();

        try {
            for (Method method : methods) {
                if (method.getParameterCount() == 0) {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                }
            }
        } catch (Exception e) {
            // 静默处理
        }

        return attributes;
    }

    @Override
    public String getMethodName() {
        return introspectedMethod.getName();
    }

    @Override
    public String getDeclaringClassName() {
        return introspectedMethod.getDeclaringClass().getName();
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return annotationAttributes.containsKey(annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return annotationAttributes.getOrDefault(annotationName, Collections.emptyMap());
    }
}
