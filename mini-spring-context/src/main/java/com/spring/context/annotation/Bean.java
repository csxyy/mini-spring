package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Bean
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:08
 * @version: v1.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    /**
     * Bean名称 - 可以指定多个名称（别名）
     */
    String[] value() default {};

    /**
     * Bean名称 - 与value相同，提供别名
     */
    String[] name() default {};

    /**
     * 是否自动装配候选
     */
    boolean autowireCandidate() default true;

    /**
     * 初始化方法名
     */
    String initMethod() default "";

    /**
     * 销毁方法名
     */
    String destroyMethod() default "";

    /**
     * 是否代理Bean方法（用于CGLIB代理）
     */
    // boolean proxyBeanMethods() default true; // 暂时不实现
}
