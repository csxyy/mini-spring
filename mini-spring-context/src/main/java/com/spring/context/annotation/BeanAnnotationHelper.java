package com.spring.context.annotation;

import com.spring.core.type.MethodMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ClassName: BeanAnnotationHelper
 * Description:
 *
 * Bean注解帮助类 - 对应Spring的BeanAnnotationHelper
 * 用于处理@Bean注解相关的工具方法
 *
 * @Author: csx
 * @Create: 2026/1/3 - 22:19
 * @version: v1.0
 */
@Slf4j
public abstract class BeanAnnotationHelper {
    /**
     * 检查方法是否被@Bean注解标注
     * 对应Spring的BeanAnnotationHelper.isBeanAnnotated方法
     */
    public static boolean isBeanAnnotated(Method method) {
        if (method == null) {
            return false;
        }

        // 直接检查方法上的@Bean注解
        boolean hasBeanAnnotation = method.isAnnotationPresent(Bean.class);

        // 也可以检查元注解，但@Bean通常不会作为元注解使用
        // 我们这里简化实现，只检查直接注解

        log.debug("检查方法 {} 是否有@Bean注解: {}", method.getName(), hasBeanAnnotation);
        return hasBeanAnnotation;
    }

    /**
     * 确定@Bean方法对应的Bean名称
     * 对应Spring的BeanAnnotationHelper.determineBeanNameFor方法
     */
    public static String determineBeanNameFor(Method beanMethod) {
        log.debug("确定@Bean方法 {} 的Bean名称", beanMethod.getName());

        // 1. 检查@Bean注解的value/name属性
        Bean beanAnnotation = beanMethod.getAnnotation(Bean.class);
        if (beanAnnotation != null) {
            // 优先使用value属性
            String[] values = beanAnnotation.value();
            if (values.length > 0 && !values[0].isEmpty()) {
                log.debug("@Bean方法 {} 使用value属性指定的名称: {}",
                        beanMethod.getName(), values[0]);
                return values[0];
            }

            // 其次使用name属性
            String[] names = beanAnnotation.name();
            if (names.length > 0 && !names[0].isEmpty()) {
                log.debug("@Bean方法 {} 使用name属性指定的名称: {}",
                        beanMethod.getName(), names[0]);
                return names[0];
            }
        }

        // 2. 默认使用方法名作为Bean名称
        String defaultName = beanMethod.getName();
        log.debug("@Bean方法 {} 使用默认名称: {}", beanMethod.getName(), defaultName);
        return defaultName;
    }

    /**
     * 从方法元数据中确定Bean名称
     * 重载版本，支持MethodMetadata
     */
    public static String determineBeanNameFor(MethodMetadata methodMetadata) {
        if (methodMetadata == null) {
            return null;
        }

        String methodName = methodMetadata.getMethodName();
        log.debug("从MethodMetadata确定@Bean方法 {} 的Bean名称", methodName);

        // 1. 检查@Bean注解属性
        Map<String, Object> beanAttributes = methodMetadata.getAnnotationAttributes(Bean.class.getName());
        if (beanAttributes != null) {
            // 检查beanNames属性（我们之前处理@Bean注解时存储的属性）
            if (beanAttributes.containsKey("beanNames")) {
                String[] beanNames = (String[]) beanAttributes.get("beanNames");
                if (beanNames.length > 0 && !beanNames[0].isEmpty()) {
                    log.debug("@Bean方法 {} 使用注解指定的名称: {}", methodName, beanNames[0]);
                    return beanNames[0];
                }
            }
        }

        // 2. 默认使用方法名
        log.debug("@Bean方法 {} 使用默认名称: {}", methodName, methodName);
        return methodName;
    }

    /**
     * 检查方法是否是@Bean方法的工厂方法
     * 对应Spring的BeanAnnotationHelper.isFactoryMethod方法
     */
    public static boolean isFactoryMethod(Method method, String beanName) {
        if (method == null || beanName == null) {
            return false;
        }

        // 检查是否是@Bean注解的方法
        if (!isBeanAnnotated(method)) {
            return false;
        }

        // 检查方法对应的Bean名称是否匹配
        String methodBeanName = determineBeanNameFor(method);
        boolean matches = beanName.equals(methodBeanName);

        log.debug("检查方法 {} 是否是Bean {} 的工厂方法: {}",
                method.getName(), beanName, matches);
        return matches;
    }

    /**
     * 提取@Bean注解的所有名称（包括别名）
     */
    public static String[] determineBeanNamesFor(Method beanMethod) {
        log.debug("提取@Bean方法 {} 的所有名称", beanMethod.getName());

        Bean beanAnnotation = beanMethod.getAnnotation(Bean.class);
        if (beanAnnotation == null) {
            return new String[]{beanMethod.getName()};
        }

        // 优先使用value属性
        String[] values = beanAnnotation.value();
        if (values.length > 0 && !values[0].isEmpty()) {
            return values;
        }

        // 其次使用name属性
        String[] names = beanAnnotation.name();
        if (names.length > 0 && !names[0].isEmpty()) {
            return names;
        }

        // 默认使用方法名
        return new String[]{beanMethod.getName()};
    }

    /**
     * 检查是否是@Configuration类的@Bean方法
     */
    public static boolean isConfigurationClassBeanMethod(Method method) {
        if (method == null) {
            return false;
        }

        // 检查方法所在的类是否有@Configuration注解
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isConfigClass = declaringClass.isAnnotationPresent(Configuration.class);
        boolean isBeanMethod = isBeanAnnotated(method);

        log.debug("检查方法 {} 是否是配置类的@Bean方法: {} (isConfigClass: {}, isBeanMethod: {})",
                method.getName(), isConfigClass && isBeanMethod, isConfigClass, isBeanMethod);

        return isConfigClass && isBeanMethod;
    }
}
