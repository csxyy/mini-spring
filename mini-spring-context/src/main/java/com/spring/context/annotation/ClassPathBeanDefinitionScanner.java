package com.spring.context.annotation;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName: ClassPathBeanDefinitionScanner
 * Description:
 *
 * 扫描类路径下的组件（@Component、@Service、@Repository、@Controller）
 * 将扫描到的类注册为BeanDefinition
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:32
 * @version: v1.0
 */
public class ClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;

    // 过滤器模式 通过includeFilters决定扫描哪些注解
    private final List<Class<? extends Annotation>> includeFilters = new ArrayList<>();

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this(registry, true);
    }

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        this.registry = registry;

        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }
    private void registerDefaultFilters() {
        // 只保留最核心的@Component注解
        this.includeFilters.add(Component.class);
        // 可以扩展：includeFilters.add(Service.class);
        // includeFilters.add(Repository.class);
        // includeFilters.add(Controller.class);
    }

    /**
     * 核心扫描方法（后面会用到）
     * @param basePackages
     * @return
     */
    public int scan(String... basePackages) {
        int beanCountBefore = getBeanDefinitionCount();

        for (String basePackage : basePackages) {
            // 扫描包路径下的所有类
            Set<Class<?>> candidateClasses = findCandidateClasses(basePackage);

            // 注册符合条件的类
            for (Class<?> candidateClass : candidateClasses) {
                if (isCandidateComponent(candidateClass)) {
                    registerBeanDefinition(candidateClass);
                }
            }
        }

        return getBeanDefinitionCount() - beanCountBefore;
    }
    private Set<Class<?>> findCandidateClasses(String basePackage) {
        // 简化版：先用空实现，后面补充扫描逻辑
        return new HashSet<>();
    }
    private boolean isCandidateComponent(Class<?> clazz) {
        // 检查类是否包含目标注解
        for (Class<? extends Annotation> filter : includeFilters) {
            if (clazz.isAnnotationPresent(filter)) {
                return true;
            }
        }
        return false;
    }
    private void registerBeanDefinition(Class<?> beanClass) {
        String beanName = generateBeanName(beanClass);
        BeanDefinition bd = new RootBeanDefinition(beanClass);
        registry.registerBeanDefinition(beanName, bd);
    }
    private String generateBeanName(Class<?> beanClass) {
        // 简单实现：类名首字母小写
        String className = beanClass.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    private int getBeanDefinitionCount() {
        // 需要registry提供获取BeanDefinition数量的方法
        return 0; // 简化实现
    }

}
