package com.spring.context.annotation;

import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: ConfigurationClassBeanDefinitionReader
 * Description: 配置类Bean定义读取器
 *
 * 负责将ConfigurationClass中定义的Bean注册为BeanDefinition
 *
 * Spring设计思想：
 *  这个类专门负责"注册"阶段，将parse阶段收集的信息转换为BeanDefinition
 *  主要处理@Bean方法、@Import引入的类等
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:06
 * @version: v1.0
 */
@Slf4j
public class ConfigurationClassBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 加载Bean定义
     */
    public void loadBeanDefinitions(Set<ConfigurationClass> configClasses) {
        log.debug("ConfigurationClassBeanDefinitionReader.loadBeanDefinitions()开始");

        for (ConfigurationClass configClass : configClasses) {
            log.debug("从ConfigurationClass加载BeanDefinitions: {}", configClass.getMetadata().getClassName());

            // TODO: 处理@Bean方法 - 将@Bean方法转换为BeanDefinition并注册
            // loadBeanDefinitionsForBeanMethods(configClass);

            // TODO: 处理@Import引入的类
            // loadBeanDefinitionsFromImportedResources(configClass);

            // TODO: 处理ImportBeanDefinitionRegistrar
            // loadBeanDefinitionsFromRegistrars(configClass);

            log.debug("ConfigurationClass BeanDefinitions加载完成: {}", configClass.getMetadata().getClassName());
        }

        log.debug("ConfigurationClassBeanDefinitionReader.loadBeanDefinitions()完成");
    }
}
