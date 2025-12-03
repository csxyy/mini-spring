package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import lombok.extern.slf4j.Slf4j;

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
     * 处理通用注解并设置到BeanDefinition中
     * 对应Spring的AnnotationConfigUtils.processCommonDefinitionAnnotations方法
     */
    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        log.debug("处理通用注解，类: {}", abd.getBeanClassName());

        AnnotationMetadata metadata = abd.getMetadata();

        // 处理@Lazy注解
        processLazyAnnotation(abd, metadata);

        // 处理@Primary注解
        processPrimaryAnnotation(abd, metadata);

        // TODO: 处理@Scope注解（后续结合AOP重构）
        processScopeAnnotation(abd, metadata);

        // TODO: 可以添加更多通用注解处理，如@Description、@Role等
        log.debug("通用注解处理完成");
    }

    /**
     * 处理@Lazy注解
     */
    private static void processLazyAnnotation(AnnotatedBeanDefinition abd, AnnotationMetadata metadata) {
        if (metadata.hasAnnotation(Lazy.class.getName())) {
            Lazy lazy = abd.getBeanClass().getAnnotation(Lazy.class);
            if (lazy != null && lazy.value()) {
                abd.setLazyInit(true);
                log.debug("设置@Lazy注解，类: {}", abd.getBeanClassName());
            }
        }
    }

    /**
     * 处理@Primary注解
     */
    private static void processPrimaryAnnotation(AnnotatedBeanDefinition abd, AnnotationMetadata metadata) {
        if (metadata.hasAnnotation(Primary.class.getName())) {
            abd.setPrimary(true);
            log.debug("设置@Primary注解，类: {}", abd.getBeanClassName());
        }
    }

    /**
     * 处理@Scope注解 - 对应Spring的ScopedProxyMode处理
     */
    private static void processScopeAnnotation(AnnotatedBeanDefinition abd, AnnotationMetadata metadata) {
        if (metadata.hasAnnotation(Scope.class.getName())) {
            Scope scope = abd.getBeanClass().getAnnotation(Scope.class);
            if (scope != null && !scope.value().isEmpty()) {
                abd.setScope(scope.value());
                log.debug("设置@Scope注解，作用域: {}，类: {}", scope.value(), abd.getBeanClassName());

                // TODO: 处理proxyMode属性，涉及AOP代理，暂时不实现
            }
        }
    }

}
