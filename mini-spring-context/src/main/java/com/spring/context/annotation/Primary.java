package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Primary
 * Description:
 *
 * @Author: csx
 * @Create: 2025/12/3 - 14:33
 * @version: v1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {
}
