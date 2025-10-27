package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Lazy
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:16
 * @version: v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {
    boolean value() default true;
}
