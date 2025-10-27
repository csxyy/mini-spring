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
}
