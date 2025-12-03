package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.BeanDefinitionHolder;
import com.spring.beans.factory.support.AbstractBeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.core.type.AnnotationMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

/**
 * ClassName: ConfigurationClassParser
 * Description: 配置类解析器
 *
 * 负责解析配置类，处理@Configuration、@ComponentScan、@Import、@Bean等注解
 *
 * Spring设计思想：
 *  这个类专门负责"解析"阶段，收集配置信息但不立即注册BeanDefinition
 *  通过ConfigurationClass对象保存解析结果
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:09
 * @version: v1.0
 */
@Slf4j
public class ConfigurationClassParser {
    private final BeanDefinitionRegistry registry;
    private final ClassPathBeanDefinitionScanner scanner;

    public ConfigurationClassParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.scanner = new ClassPathBeanDefinitionScanner(registry, false);
    }

    /**
     * 解析配置类
     * @param configCandidates 配置类候选者列表
     * @param configurationClasses 用于存储解析结果的ConfigurationClass集合
     */
    public void parse(List<BeanDefinitionHolder> configCandidates, Set<ConfigurationClass> configurationClasses) {
        log.debug("ConfigurationClassParser.parse()开始，处理 {} 个候选者", configCandidates.size());

        for (BeanDefinitionHolder candidate : configCandidates) {
            BeanDefinition bd = candidate.getBeanDefinition();

            // 关键修改：充分利用AnnotatedBeanDefinition的能力
            if (bd instanceof AnnotatedBeanDefinition abd) {
                log.debug("解析配置类: {}", abd.getMetadata().getClassName());

                // 创建ConfigurationClass对象，直接传递AnnotatedBeanDefinition
                ConfigurationClass configClassObj = new ConfigurationClass(abd, candidate.getBeanName());

                // 处理配置类中的各种注解
                processConfigurationClass(configClassObj);

                // 添加到结果集
                configurationClasses.add(configClassObj);

            } else {
                log.warn("跳过非注解BeanDefinition: {}", candidate.getBeanName());
            }
        }

        log.debug("ConfigurationClassParser.parse()完成");
    }

    /**
     * 处理单个配置类 - 对应Spring原版的processConfigurationClass方法
     * 这个方法会递归处理配置类层次结构
     */
    private void processConfigurationClass(ConfigurationClass configClass) {
        AnnotatedBeanDefinition abd = configClass.getAnnotatedBeanDefinition();
        log.debug("处理配置类: {}", abd.getMetadata().getClassName());

        // 处理@ComponentScan注解
        if (abd.getMetadata().hasAnnotation(ComponentScan.class.getName())) {
            processComponentScan(configClass);
        }

        // TODO: 处理@Import注解
        // processImports(configClass);

        // TODO: 处理@Bean方法 - 注意：这里只收集@Bean方法信息，不注册BeanDefinition
        // processBeanMethods(configClass);

        // TODO: 处理接口默认方法
        // processInterfaces(configClass);

        // TODO: 处理父类
        // processSuperClass(configClass);

        log.debug("配置类处理完成: {}", abd.getMetadata().getClassName());

    }

    /**
     * 处理@ComponentScan注解
     * 注意：这里会立即执行扫描并注册BeanDefinition，这是Spring的设计
     */
    private void processComponentScan(ConfigurationClass configClass) {
        AnnotatedBeanDefinition abd = configClass.getAnnotatedBeanDefinition();
        Class<?> configClassObj = abd.getBeanClass();

        ComponentScan componentScan = configClassObj.getAnnotation(ComponentScan.class);
        if (componentScan != null) {
            log.debug("处理配置类中的@ComponentScan注解: {}", abd.getMetadata().getClassName());

            // Step 1: 创建并配置扫描器（对应Spring的createScanner方法）
            ClassPathBeanDefinitionScanner configuredScanner = createConfiguredScanner(componentScan, configClassObj);

            // Step 2: 获取基础包路径
            String[] basePackages = getBasePackages(componentScan, configClassObj);
            log.debug("扫描基础包路径: {}", Arrays.toString(basePackages));

            if (basePackages.length > 0) {
                // Step 3: 排除配置类自身（重要！）
                excludeDeclaringClass(configuredScanner, configClassObj);

                // Step 4: 执行扫描
                int scannedCount = configuredScanner.scan(basePackages);
                log.debug("组件扫描完成，注册了 {} 个新BeanDefinition", scannedCount);
            } else {
                log.warn("@ComponentScan未指定任何基础包路径");
            }
        }
    }

    /**
     * 创建并配置扫描器 - 对应Spring原版创建和配置扫描器的逻辑
     * 包含：Bean名称生成器、作用域代理、资源模式、过滤器等配置
     */
    private ClassPathBeanDefinitionScanner createConfiguredScanner(ComponentScan componentScan, Class<?> configClass) {
        log.debug("创建并配置ClassPathBeanDefinitionScanner");

        // Step 1: 创建扫描器实例（不使用默认过滤器，因为我们要根据@ComponentScan配置）
        ClassPathBeanDefinitionScanner configuredScanner = new ClassPathBeanDefinitionScanner(registry, false);

        // Step 2: Bean名称生成器 - 在扫描器内部简单实现，不创建复杂生成器
        // 我们的generateBeanName方法已经满足基本需求

        // Step 3: 作用域代理配置 - 暂时跳过，原因：
        // - 涉及AOP和代理机制，比较复杂
        // - 在mini项目中可以先关注核心IOC流程
        // - Spring原版通过ScopedProxyMode配置，我们暂时用默认行为
        log.debug("作用域代理配置暂不实现，使用默认行为");

        // Step 4: 资源匹配模式 - 在扫描器内部实现更好
        // 这样扫描器可以独立工作，不依赖外部配置

        // Step 5-6: 配置包含和排除过滤器 - 核心功能
        configureIncludeExcludeFilters(configuredScanner, componentScan);

        // Step 7: 懒加载配置 - 在扫描器内部实现更合适
        // 因为懒加载是BeanDefinition的属性，在注册时设置

        log.debug("扫描器配置完成");
        return configuredScanner;
    }

    /**
     * 配置包含和排除过滤器 - 对应Spring原版配置过滤器的逻辑
     */
    private void configureIncludeExcludeFilters(ClassPathBeanDefinitionScanner scanner, ComponentScan componentScan) {
        log.debug("配置包含和排除过滤器");

        // 包含过滤器配置
        // Spring原版会处理@ComponentScan的includeFilters属性
        // 我们简化实现：如果用户没有自定义包含过滤器，使用默认的@Component
        if (componentScan.includeFilters().length == 0) {
            // 使用默认过滤器
            scanner.registerDefaultFilters();
        } else {
            // TODO: 处理自定义包含过滤器（后续实现）
            log.debug("自定义包含过滤器暂不实现，使用默认过滤器");
            scanner.registerDefaultFilters();
        }

        // 排除过滤器配置
        // Spring原版会处理@ComponentScan的excludeFilters属性
        // 我们简化实现：暂时不实现排除过滤器
        log.debug("排除过滤器功能暂不实现");
    }

    /**
     * 排除配置类自身 - 对应Spring原版排除declaringClass的逻辑
     *
     * 设计思想解释：
     * 在Spring中，当在一个配置类上使用@ComponentScan时，通常不希望扫描到这个配置类自身。
     * 因为配置类通常已经通过其他方式（如直接注册）被容器管理了，重复扫描会导致：
     * 1. Bean重复定义
     * 2. 可能的循环依赖问题
     * 3. 意外的Bean替换
     *
     * 例如：
     * @Configuration
     * @ComponentScan  // 如果不排除自身，会再次扫描到@Configuration注解的当前类
     * public class AppConfig {}
     *
     * 在mini项目中需要实现这个逻辑，因为它影响扫描结果的正确性。
     */
    private void excludeDeclaringClass(ClassPathBeanDefinitionScanner scanner, Class<?> declaringClass) {
        log.debug("添加配置类自身排除过滤器: {}", declaringClass.getName());

        // 在扫描器中添加排除逻辑
        // 我们可以在扫描器的isCandidateComponent方法中添加这个检查
        // 这里我们通过一个简单的方式：记录要排除的类，在扫描时跳过
        // 实际Spring使用更复杂的TypeFilter机制，我们这里简化实现

        // 由于我们的扫描器结构简单，可以通过扩展扫描器来支持这个功能
        // 这里我们先记录日志，后续在扫描器实现中处理
        log.debug("配置类自身排除功能将在扫描器doScan方法中实现");
    }

    private String[] getBasePackages(ComponentScan componentScan, Class<?> configClass) {
        log.debug("开始解析@ComponentScan的基础包路径");

        // 步骤1：合并value和basePackages - 它们在Spring中是别名关系
        Set<String> basePackageSet = new LinkedHashSet<>();

        // 添加value属性指定的包路径
        String[] valuePackages = componentScan.value();
        if (valuePackages.length > 0) {
            Collections.addAll(basePackageSet, valuePackages);
            log.debug("从value属性获取包路径: {}", Arrays.toString(valuePackages));
        }

        // 添加basePackages属性指定的包路径
        String[] basePackages = componentScan.basePackages();
        if (basePackages.length > 0) {
            Collections.addAll(basePackageSet, basePackages);
            log.debug("从basePackages属性获取包路径: {}", Arrays.toString(basePackages));
        }

        // 步骤2：如果显式指定了包路径，直接返回
        if (!basePackageSet.isEmpty()) {
            String[] result = basePackageSet.toArray(new String[0]);
            log.debug("合并后的基础包路径: {}", Arrays.toString(result));
            return result;
        }

        // 步骤3：如果没有显式指定包路径，使用配置类所在包作为默认路径
        String defaultPackage = configClass.getPackage().getName();
        log.debug("未显式指定包路径，使用配置类所在包作为默认路径: {}", defaultPackage);
        return new String[]{defaultPackage};
    }
}
