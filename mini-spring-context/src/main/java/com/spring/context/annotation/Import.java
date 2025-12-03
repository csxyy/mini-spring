package com.spring.context.annotation;

import java.lang.annotation.*;

/**
 * ClassName: Import
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:58
 * @version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

    Class<?>[] value();

}
