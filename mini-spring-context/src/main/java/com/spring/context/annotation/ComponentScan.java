package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: ComponentScan
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:09
 * @version: v1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {
    String[] basePackages() default {};
}
