package com.spring.core.type;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * ClassName: StandardAnnotationMetadata
 * Description: 标准注解元数据实现
 *
 * 作用：封装类的注解信息，提供统一的注解查询接口
 * 设计思想：将注解扫描和解析逻辑封装起来，让业务代码不需要直接处理反射API
 *
 * 为什么需要：如果不定义这个类，每次需要检查注解时都要写重复的反射代码，
 * 而且无法缓存注解信息，性能差且代码混乱
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:12
 * @version: v1.0
 */
@Slf4j
@Data
public class StandardAnnotationMetadata implements AnnotationMetadata{

    /** 被扫描的类对象 */
    private final Class<?> introspectedClass;

    /** 注解属性缓存：注解类型 -> {属性名 -> 属性值} */
    private final Map<String, Map<String, Object>> annotationAttributes;

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        this.introspectedClass = introspectedClass;
        this.annotationAttributes = new HashMap<>();
        scanAnnotations(); // 立即扫描，避免懒加载的复杂性
    }

    /**
     * 扫描类上的所有注解（包括继承的注解）
     * 不包含任何Spring特定的业务逻辑
     */
    private void scanAnnotations() {
        log.debug("开始扫描类 {} 上的注解", introspectedClass.getName());

        // getDeclaredAnnotations()：获取直接声明的所有注解（不包括继承的）
        // getAnnotations()：获取所有可继承的注解（需要注解有@Inherited元注解）
        Annotation[] annotations = introspectedClass.getDeclaredAnnotations();

        log.debug("类 {} 上找到 {} 个注解", introspectedClass.getName(), annotations.length);

        for (Annotation annotation : annotations) {
            String annotationType = annotation.annotationType().getName();
            Map<String, Object> attributes = extractAnnotationAttributes(annotation);

            annotationAttributes.put(annotationType, attributes);

            log.debug("扫描到注解: {}，属性: {}", annotationType, attributes);
        }

        log.debug("类 {} 注解扫描完成，共扫描到 {} 个注解",
                introspectedClass.getName(), annotationAttributes.size());
    }

    /**
     * 提取注解属性 - 通用实现，不依赖具体注解
     */
    private Map<String, Object> extractAnnotationAttributes(Annotation annotation) {
        Map<String, Object> attributes = new HashMap<>();

        try {
            Method[] methods = annotation.annotationType().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterCount() == 0 &&
                        method.getReturnType() != void.class) {
                    try {
                        Object value = method.invoke(annotation);
                        attributes.put(method.getName(), value);
                    } catch (Exception e) {
                        log.debug("获取注解属性失败: {}.{}",
                                annotation.annotationType().getSimpleName(), method.getName());
                        // 忽略单个属性获取失败，继续处理其他属性
                    }
                }
            }
        } catch (Exception e) {
            log.error("提取注解属性时发生异常: {}", annotation.annotationType().getName(), e);
        }

        return attributes;
    }


    // ============ 接口方法实现 ============

    @Override
    public String getClassName() {
        return introspectedClass.getName();
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        // 首先检查直接注解，直接从缓存中查找，O(1)时间复杂度
        if (annotationAttributes.containsKey(annotationName)) {
            return true;
        }

        // 使用栈来模拟递归，避免栈溢出
        Stack<Class<? extends Annotation>> stack = new Stack<>();
        Set<Class<? extends Annotation>> visited = new HashSet<>();

        // 将当前类的所有直接注解加入栈中
        for (Map.Entry<String, Map<String, Object>> entry : annotationAttributes.entrySet()) {
            try {
                Class<?> annotationClass = Class.forName(entry.getKey());
                if (annotationClass.isAnnotation()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotationType =
                            (Class<? extends Annotation>) annotationClass;

                    if (!isJavaLangAnnotation(annotationType)) {
                        stack.push(annotationType);
                        visited.add(annotationType);
                    }
                }
            } catch (ClassNotFoundException e) {
                log.debug("无法加载注解类: {}", entry.getKey());
            }
        }

        // 迭代处理注解关系
        while (!stack.isEmpty()) {
            Class<? extends Annotation> currentAnnotation = stack.pop();

            // 检查当前注解是否为目标注解
            if (currentAnnotation.getName().equals(annotationName)) {
                return true;
            }

            // 将当前注解的元注解加入栈中
            Annotation[] metaAnnotations = currentAnnotation.getDeclaredAnnotations();
            for (Annotation metaAnnotation : metaAnnotations) {
                Class<? extends Annotation> metaAnnotationType = metaAnnotation.annotationType();

                // 跳过Java内置注解和已访问的注解
                if (isJavaLangAnnotation(metaAnnotationType) || visited.contains(metaAnnotationType)) {
                    continue;
                }

                stack.push(metaAnnotationType);
                visited.add(metaAnnotationType);
            }
        }

        return false;
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        log.debug("获取注解属性: {}，类: {}", annotationName, introspectedClass.getName());

        // 检查直接注解
        if (annotationAttributes.containsKey(annotationName)) {
            Map<String, Object> attributes = annotationAttributes.get(annotationName);
            log.debug("找到直接注解属性: {}", attributes);
            return processAttributes(attributes, classValuesAsString);
        }

        // 检查元注解（递归）
        Map<String, Object> metaAttributes = getMetaAnnotationAttributes(annotationName, classValuesAsString);
        if (metaAttributes != null) {
            log.debug("找到元注解属性: {}", metaAttributes);
            return metaAttributes;
        }

        log.debug("未找到注解: {}", annotationName);
        return null;
    }

    /**
     * 获取元注解的属性值
     */
    private Map<String, Object> getMetaAnnotationAttributes(String targetAnnotationName, boolean classValuesAsString) {
        Set<Class<? extends Annotation>> visited = new HashSet<>();

        for (String directAnnotationName : annotationAttributes.keySet()) {
            try {
                Class<?> annotationClass = Class.forName(directAnnotationName);
                if (annotationClass.isAnnotation()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotationType = (Class<? extends Annotation>) annotationClass;

                    Map<String, Object> metaAttrs = findMetaAnnotationAttributes(
                            annotationType, targetAnnotationName, visited, classValuesAsString);
                    if (metaAttrs != null) {
                        return metaAttrs;
                    }
                }
            } catch (ClassNotFoundException e) {
                log.debug("无法加载注解类: {}", directAnnotationName);
            }
        }

        return null;
    }

    /**
     * 递归查找元注解属性
     */
    private Map<String, Object> findMetaAnnotationAttributes(
            Class<? extends Annotation> annotationType,
            String targetAnnotationName,
            Set<Class<? extends Annotation>> visited,
            boolean classValuesAsString) {

        // 避免循环依赖
        if (visited.contains(annotationType)) {
            return null;
        }
        visited.add(annotationType);

        // 获取当前注解的元注解
        Annotation[] metaAnnotations = annotationType.getDeclaredAnnotations();
        for (Annotation metaAnnotation : metaAnnotations) {
            Class<? extends Annotation> metaAnnotationType = metaAnnotation.annotationType();
            String metaAnnotationName = metaAnnotationType.getName();

            // 如果是目标注解
            if (metaAnnotationName.equals(targetAnnotationName)) {
                Map<String, Object> attributes = extractAnnotationAttributes(metaAnnotation);
                return processAttributes(attributes, classValuesAsString);
            }

            // 递归查找
            Map<String, Object> nestedAttrs = findMetaAnnotationAttributes(
                    metaAnnotationType, targetAnnotationName, visited, classValuesAsString);
            if (nestedAttrs != null) {
                return nestedAttrs;
            }
        }

        return null;
    }

    /**
     * 处理属性值（如将Class转换为字符串）
     */
    private Map<String, Object> processAttributes(Map<String, Object> attributes, boolean classValuesAsString) {
        if (!classValuesAsString || attributes == null) {
            return attributes;
        }

        Map<String, Object> processed = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Class) {
                processed.put(entry.getKey(), ((Class<?>) value).getName());
            } else if (value instanceof Class[]) {
                Class<?>[] classes = (Class<?>[]) value;
                String[] classNames = new String[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    classNames[i] = classes[i].getName();
                }
                processed.put(entry.getKey(), classNames);
            } else {
                processed.put(entry.getKey(), value);
            }
        }

        return processed;
    }

    /**
     * 判断是否为Java语言内置的注解
     * 这些注解不会包含业务逻辑的元注解关系，可以跳过以避免不必要的递归
     */
    private boolean isJavaLangAnnotation(Class<? extends Annotation> annotationType) {
        String packageName = annotationType.getPackage().getName();
        return packageName.startsWith("java.lang.annotation") ||
                packageName.startsWith("java.lang");
    }

    @Override
    public Set<String> getAnnotationTypes() {
        return Collections.unmodifiableSet(annotationAttributes.keySet());
    }

    @Override
    public boolean isInterface() {
        return introspectedClass.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(introspectedClass.getModifiers());
    }
}
