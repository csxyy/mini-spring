package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.MethodMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ClassName: AnnotationConfigUtils
 * Description:
 *
 * 通用注解处理器 - 处理@Lazy、@Primary、@Scope等通用注解
 *
 * @Author: csx
 * @Create: 2025/12/3 - 14:29
 * @version: v1.0
 */
@Slf4j
public class AnnotationConfigUtils {

    /**
     * 处理通用注解 - 针对AnnotationMetadata（类级别） 并设置到BeanDefinition中
     * 对应Spring的AnnotationConfigUtils.processCommonDefinitionAnnotations方法
     */

    /**
     * 处理通用注解 - 针对AnnotationMetadata（类级别）
     */
    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        log.debug("处理通用注解，类: {}", abd.getBeanClassName());
        AnnotationMetadata metadata = abd.getMetadata();
        processCommonDefinitionAnnotationsInternal(abd, metadata);
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, MethodMetadata metadata) {
        log.debug("处理方法上的通用注解，方法: {}", metadata.getMethodName());
        processCommonDefinitionAnnotationsInternal(abd, metadata);
    }

    /**
     * 内部通用处理方法 - 处理AnnotationMetadata和MethodMetadata的公共逻辑
     */
    private static void processCommonDefinitionAnnotationsInternal(AnnotatedBeanDefinition abd, Object metadata) {
        // 处理@Lazy注解
        processLazyAnnotation(abd, metadata);

        // 处理@Primary注解
        processPrimaryAnnotation(abd, metadata);

        // TODO: 处理@Scope注解（后续结合AOP重构，这里的处理逻辑会删除）
        processScopeAnnotation(abd, metadata);

        // TODO: 可以添加更多通用注解处理，如@Description、@Role等
        log.debug("通用注解处理完成");
    }

    /**
     * 处理@Lazy注解
     */
    private static void processLazyAnnotation(AnnotatedBeanDefinition abd, Object metadata) {
        boolean hasAnnotation = false;
        boolean lazyValue = false;

        if (metadata instanceof AnnotationMetadata am) {
            hasAnnotation = am.isAnnotated(Lazy.class.getName());
            if (hasAnnotation && abd.getBeanClass() != null) {
                Lazy lazy = abd.getBeanClass().getAnnotation(Lazy.class);
                if (lazy != null) {
                    lazyValue = lazy.value();
                }
            }
        } else if (metadata instanceof MethodMetadata mm) {
            hasAnnotation = mm.isAnnotated(Lazy.class.getName());
            if (hasAnnotation) {
                Map<String, Object> attributes = mm.getAnnotationAttributes(Lazy.class.getName());
                if (attributes != null && attributes.containsKey("value")) {
                    lazyValue = (Boolean) attributes.get("value");
                }
            }
        }

        if (hasAnnotation) {
            abd.setLazyInit(lazyValue);
            String targetName = (metadata instanceof MethodMetadata mm) ?
                    "方法: " + mm.getMethodName() : "类: " + abd.getBeanClassName();
            log.debug("设置@Lazy注解，值: {}，{}", lazyValue, targetName);
        }
    }

    /**
     * 处理@Primary注解
     */
    private static void processPrimaryAnnotation(AnnotatedBeanDefinition abd, Object metadata) {
        boolean hasAnnotation = false;

        if (metadata instanceof AnnotationMetadata am) {
            hasAnnotation = am.isAnnotated(Primary.class.getName());
        } else if (metadata instanceof MethodMetadata mm) {
            hasAnnotation = mm.isAnnotated(Primary.class.getName());
        }

        if (hasAnnotation) {
            abd.setPrimary(true);
            String targetName = (metadata instanceof MethodMetadata mm) ?
                    "方法: " + mm.getMethodName() : "类: " + abd.getBeanClassName();
            log.debug("设置@Primary注解，{}", targetName);
        }
    }

    /**
     * 处理@Scope注解 - 对应Spring的ScopedProxyMode处理
     */
    private static void processScopeAnnotation(AnnotatedBeanDefinition abd, Object metadata) {
        String scopeValue = null;

        if (metadata instanceof AnnotationMetadata am) {
            if (am.isAnnotated(Scope.class.getName()) && abd.getBeanClass() != null) {
                Scope scope = abd.getBeanClass().getAnnotation(Scope.class);
                if (scope != null && !scope.value().isEmpty()) {
                    scopeValue = scope.value();
                }
            }
        } else if (metadata instanceof MethodMetadata mm) {
            if (mm.isAnnotated(Scope.class.getName())) {
                Map<String, Object> attributes = mm.getAnnotationAttributes(Scope.class.getName());
                if (attributes != null && attributes.containsKey("value")) {
                    scopeValue = (String) attributes.get("value");
                }
            }
        }

        if (scopeValue != null && !scopeValue.isEmpty()) {
            abd.setScope(scopeValue);
            String targetName = (metadata instanceof MethodMetadata mm) ?
                    "方法: " + mm.getMethodName() : "类: " + abd.getBeanClassName();
            log.debug("设置@Scope注解，作用域: {}，{}", scopeValue, targetName);

            // TODO: 处理proxyMode属性，涉及AOP代理，暂时不实现（后续此方法会删除）
        }
    }

}
