package com.spring.stereotype;

import java.lang.annotation.*;

/**
 * ClassName: Component
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:00
 * @version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    /**
     * 设置beanName：默认是类名小写
     */
    String value() default "";
}
