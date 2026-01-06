package com.spring.stereotype;

import java.lang.annotation.*;

/**
 * ClassName: Repository
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:20
 * @version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    /**
     * 设置beanName：默认是类名小写
     */
    String value() default "";
}
