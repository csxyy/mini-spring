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

    /**
     * value 和 basePackages 都是扫描路径
     */
    String[] value() default {};

    String[] basePackages() default {};

    /**
     * 基础包类 - 指定类，扫描这些类所在的包
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 包含过滤器 - 暂时返回空数组
     * 注意：这里我们使用Class<?>[]而不是复杂的Filter类型
     */
    Class<?>[] includeFilters() default {};

    /**
     * 排除过滤器 - 暂时返回空数组
     */
    Class<?>[] excludeFilters() default {};

    /**
     * 是否使用默认过滤器
     */
    boolean useDefaultFilters() default true;

    /**
     * 懒加载初始化：设置了，被扫描到的所有组件都是懒加载
     */
    boolean lazyInit() default false;
}
