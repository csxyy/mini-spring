package com.spring.context.annotation;

import com.spring.stereotype.Component;

import java.lang.annotation.*;

/**
 * ClassName: Configuration
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:46
 * @version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
}
