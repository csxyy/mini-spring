package com.spring.context.annotation;

/**
 * ClassName: AnnotationConfigRegistry
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:35
 * @version: v1.0
 */
public interface AnnotationConfigRegistry {
    void register(Class<?>... componentClasses);

    void scan(String... basePackages);
}
