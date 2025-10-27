package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.RootBeanDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: AnnotatedBeanDefinitionReader
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:32
 * @version: v1.0
 */
@Slf4j
public class AnnotatedBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;


    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        // 1.保存BeanDefinitionRegistry引用
        this.registry = registry;

        // 2.只注册最核心的处理器
        registerAnnotationConfigProcessors();
    }
    private void registerAnnotationConfigProcessors() {
        // 1. 注册ConfigurationClassPostProcessor：负责解析@Configuration类 处理@ComponentScan、@Bean、@Import等注解
        if (!registry.containsBeanDefinition("configurationClassPostProcessor")) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            registry.registerBeanDefinition("configurationClassPostProcessor", def);
        }

        // 2. 注册AutowiredAnnotationBeanPostProcessor：处理@Autowired、@Value注解的自动装配
        if (!registry.containsBeanDefinition("autowiredAnnotationProcessor")) {
            RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            registry.registerBeanDefinition("autowiredAnnotationProcessor", def);
        }
    }


    /**
     * 注册BeanDefinition
     * @param componentClasses
     */
    public void register(Class<?>... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }
    public void registerBean(Class<?> beanClass) {
        // 极度简化版：只做最核心的注册
        doRegisterBean(beanClass);
    }
    private void doRegisterBean(Class<?> beanClass) {
        // 1. 创建BeanDefinition（简化版）
        BeanDefinition abd = new RootBeanDefinition(beanClass);

        // 2. 生成Bean名称（简化版）
        String beanName = generateBeanName(beanClass);

        // 3. 处理基础注解（简化版）
        processBasicAnnotations(abd, beanClass);

        // 4. 注册BeanDefinition
        registry.registerBeanDefinition(beanName, abd);

        log.info("注册Bean: {} -> {}", beanName, beanClass.getSimpleName());
    }
    private String generateBeanName(Class<?> beanClass) {
        // 简单实现：类名首字母小写
        String className = beanClass.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    private void processBasicAnnotations(BeanDefinition bd, Class<?> beanClass) {
        // 处理@Lazy注解
        if (beanClass.isAnnotationPresent(Lazy.class)) {
            bd.setLazyInit(true);
        }

        // 处理@Scope注解
        Scope scopeAnnotation = beanClass.getAnnotation(Scope.class);
        if (scopeAnnotation != null) {
            bd.setScope(scopeAnnotation.value());
        }

        // 可以扩展：@Primary, @DependsOn等
    }

}
