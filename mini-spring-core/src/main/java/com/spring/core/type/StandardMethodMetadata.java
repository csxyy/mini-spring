package com.spring.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    private final Method method;

    /** 方法注解缓存 */
    private final Map<String, Map<String, Object>> annotationAttributes;

    public StandardMethodMetadata(Method method) {
        this.method = method;
        this.annotationAttributes = new HashMap<>();
        scanMethodAnnotations();
    }

    /**
     * 扫描方法上的注解
     */
    private void scanMethodAnnotations() {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            String annotationType = annotation.annotationType().getName();
            Map<String, Object> attributes = extractAnnotationAttributes(annotation);
            annotationAttributes.put(annotationType, attributes);
        }
    }

    /**
     * 提取注解属性
     */
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

    /**
     * 设置注解属性
     */
    public void setAnnotationAttributes(String annotationName, Map<String, Object> attributes) {
        annotationAttributes.put(annotationName, attributes);
    }

    @Override
    public String getMethodName() {
        return method.getName();
    }

    @Override
    public String getDeclaringClassName() {
        return method.getDeclaringClass().getName();
    }

    @Override
    public String getReturnTypeName() {
        return method.getReturnType().getName();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(method.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(method.getModifiers());
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return annotationAttributes.containsKey(annotationName) ||
                method.isAnnotationPresent(getAnnotationClass(annotationName));
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return annotationAttributes.get(annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        Map<String, Object> attributes = annotationAttributes.get(annotationName);
        if (attributes == null || !classValuesAsString) {
            return attributes;
        }

        // 转换Class值为字符串
        Map<String, Object> converted = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Class) {
                converted.put(entry.getKey(), ((Class<?>) value).getName());
            } else if (value instanceof Class[]) {
                Class<?>[] classes = (Class<?>[]) value;
                String[] classNames = new String[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    classNames[i] = classes[i].getName();
                }
                converted.put(entry.getKey(), classNames);
            } else {
                converted.put(entry.getKey(), value);
            }
        }
        return converted;
    }

    /**
     * 获取Method对象
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取返回类型
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * 获取参数类型
     */
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    /**
     * 获取注解类
     */
    private Class<? extends Annotation> getAnnotationClass(String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationClass =
                    (Class<? extends Annotation>) Class.forName(annotationName);
            return annotationClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
