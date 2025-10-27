package com.spring.context.annotation;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.spring.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ConfigurationClassPostProcessor
 * Description: 实现核心处理器
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:04
 * @version: v1.0
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        // 简化版：只处理配置类扫描
        processConfigClasses(registry);
    }

    private void processConfigClasses(BeanDefinitionRegistry registry) {
        // 找出所有配置类的BeanDefinition
        List<String> configBeanNames = findConfigurationBeanNames(registry);

        for (String beanName : configBeanNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            Class<?> configClass = beanDefinition.getBeanClass();

            // 解析配置类注解
            parseConfigurationClass(configClass, registry);
        }
    }

    private List<String> findConfigurationBeanNames(BeanDefinitionRegistry registry) {
        // 简单实现：找出所有@Configuration标注的类
        List<String> configBeans = new ArrayList<>();
        // 这里先写死，后面实现扫描逻辑
        if (registry.containsBeanDefinition("appConfig")) {
            configBeans.add("appConfig");
        }
        return configBeans;
    }

    private void parseConfigurationClass(Class<?> configClass, BeanDefinitionRegistry registry) {
        // 解析@Bean方法并注册
        parseBeanMethods(configClass, registry);
        // 解析@ComponentScan并扫描包
        parseComponentScan(configClass, registry);
    }

    private void parseBeanMethods(Class<?> configClass, BeanDefinitionRegistry registry) {
        // 遍历所有@Bean方法，注册为BeanDefinition
        for (Method method : configClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                registerBeanMethod(method, registry);
            }
        }
    }

    private void parseComponentScan(Class<?> configClass, BeanDefinitionRegistry registry) {
        ComponentScan scanAnnotation = configClass.getAnnotation(ComponentScan.class);
        if (scanAnnotation != null) {
            // 扫描指定包路径，注册@Component类
            String[] basePackages = scanAnnotation.basePackages();
            // 调用ClassPathBeanDefinitionScanner扫描
        }
    }

    private void registerBeanMethod(Method method, BeanDefinitionRegistry registry) {
        // 创建方法对应的BeanDefinition
        String beanName = method.getName();
        BeanDefinition bd = new RootBeanDefinition(method.getReturnType());
        registry.registerBeanDefinition(beanName, bd);
    }

}
