package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Scope
 * Description: Bean实例的作用域范围
 *
 * 作用域注解
 * 用于定义Bean的作用域（单例、原型、会话等）
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:17
 * @version: v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    /**
     * 作用域名称（目前只实现了singleton和prototype）
     * 常用值：singleton（默认）、prototype、request、session等
     */
    String value() default "";

    /**
     * 作用域代理模式
     * 默认不代理
     */
    // ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT; // 暂时不实现
}
