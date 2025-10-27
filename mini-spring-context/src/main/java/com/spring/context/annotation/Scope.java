package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Scope
 * Description: Bean实例的作用域范围
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:17
 * @version: v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    // singleton 单例；prototype 多例
    String value() default "";
}
