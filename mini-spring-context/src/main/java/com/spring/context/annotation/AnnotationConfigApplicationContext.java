package com.spring.context.annotation;

import com.spring.context.support.GenericApplicationContext;

import java.util.Arrays;

/**
 * ClassName: AnnotationConfigApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:29
 * @version: v1.0
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

    private final AnnotatedBeanDefinitionReader reader;

    private final ClassPathBeanDefinitionScanner scanner;

    public AnnotationConfigApplicationContext(){
        this.reader = new AnnotatedBeanDefinitionReader(this);

        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
        this();
        register(componentClasses);
        refresh();
    }

    @Override
    public void register(Class<?>... componentClasses) {

        // 利用AnnotatedBeanDefinitionReader将componentClass注册为BeanDefinition
        // 这是直接将componentClasses注册到Spring容器中，不涉及扫描，refresh时才会扫描
        this.reader.register(componentClasses);
    }

    @Override
    public void scan(String... basePackages) {

        this.scanner.scan(basePackages);
    }

}
