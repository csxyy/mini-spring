package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.BeanDefinitionHolder;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName: ConfigurationClassPostProcessor
 * Description: 配置类后置处理器
 *
 * 设计思想：
 *  1. BeanDefinitionRegistryPostProcessor接口允许在BeanFactory标准初始化后修改BeanDefinition
 *  2. 负责处理@Configuration, @ComponentScan, @Bean, @Import等注解
 *  3. 通过解析配置类来动态注册更多的BeanDefinition
 *
 * @Author: csx
 * @Create: 2025/10/25 - 1:04
 * @version: v1.0
 */
@Slf4j
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
//    private BeanDefinitionRegistry registry;
    private ConfigurationClassParser parser;
    private ConfigurationClassBeanDefinitionReader reader;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        log.debug("=== 开始ConfigurationClassPostProcessor处理 ===");
//        this.registry = registry;

        // 初始化解析器和读取器
        this.parser = new ConfigurationClassParser(registry);
        this.reader = new ConfigurationClassBeanDefinitionReader(registry);

        // Spring核心三步骤：
        // 1. 检查配置类候选者
        // 2. 解析配置类
        // 3. 加载BeanDefinition
        processConfigBeanDefinitions(registry);

        log.debug("=== ConfigurationClassPostProcessor处理完成 ===");
    }

    /**
     * 将配置类的处理分为两个阶段：
     *      阶段1：解析配置类，收集信息（parse）
     *      阶段2：注册配置类中定义的Bean（loadBeanDefinitions）
     */
    private void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        log.debug("开始处理配置类BeanDefinitions");

        // Step 1: 识别所有配置类候选者
        List<BeanDefinitionHolder> configCandidates = findConfigurationCandidates(registry);
        log.debug("找到 {} 个配置类候选者", configCandidates.size());

        if (configCandidates.isEmpty()) {
            return;
        }

        // Step 2: 解析配置类
        // 这个阶段收集配置类信息，但不注册BeanDefinition
        Set<ConfigurationClass> configClasses = new LinkedHashSet<>();
        try {
            log.debug("开始调用parser.parse()解析配置类");
            parser.parse(configCandidates, configClasses);
            log.debug("parser.parse()解析完成，得到 {} 个ConfigurationClass", configClasses.size());
        } catch (Exception ex) {
            log.error("配置类解析失败", ex);
            throw new RuntimeException("配置类解析失败", ex);
        }

        // Step 3: 注册BeanDefinitions
        // 这个阶段真正将@Bean方法、@Import等转换为BeanDefinition并注册
        if (!configClasses.isEmpty()) {
            try {
                log.debug("开始调用reader.loadBeanDefinitions()注册BeanDefinitions");
                reader.loadBeanDefinitions(configClasses);
                log.debug("reader.loadBeanDefinitions()注册完成");
            } catch (Exception ex) {
                log.error("BeanDefinition注册失败", ex);
                throw new RuntimeException("BeanDefinition注册失败", ex);
            }
        }

        log.debug("配置类BeanDefinitions处理完成");
    }

    /**
     * 查找配置类候选者
     */
    private List<BeanDefinitionHolder> findConfigurationCandidates(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
        String[] candidateNames = registry.getBeanDefinitionNames();

        log.debug("扫描 {} 个BeanDefinition，寻找配置类候选者", candidateNames.length);

        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);

            // 使用ConfigurationClassUtils检查是否为配置类候选者
            if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
                log.debug("✅ 识别为配置类候选者: {}", beanName);
            }
        }

        return configCandidates;
    }
}
