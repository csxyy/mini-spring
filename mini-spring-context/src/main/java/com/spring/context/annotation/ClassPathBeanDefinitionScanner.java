package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.BeanDefinitionHolder;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import com.spring.stereotype.Component;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

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
@Slf4j
@Data
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    private final BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this(registry, true);
    }

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        this.registry = registry;

        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }

    /**
     * 核心扫描方法
     * @param basePackages
     * @return
     */
    public int scan(String... basePackages) {
        int beanCountBefore = getBeanDefinitionCount();

        doScan(basePackages);

        return getBeanDefinitionCount() - beanCountBefore;
    }

    public void doScan(String... basePackages) {
        log.debug("开始执行组件扫描，基础包: {}", Arrays.toString(basePackages));

        for (String basePackage : basePackages) {
            log.debug("扫描包: {}", basePackage);

            // 扫描基础包下的所有候选组件
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            log.debug("在包 {} 中找到 {} 个候选组件", basePackage, candidates.size());

            // 遍历所有找到的候选组件
            for (BeanDefinition candidate : candidates) {
                String beanClassName = candidate.getBeanClassName();
                log.debug("处理候选Bean: {}", beanClassName);

                // 第一步：解析作用域元数据（@Scope注解）
                // 这里占时不实现，因为涉及AOP，但@Scope生成多例Bean的逻辑在第四步一起实现，后面在重构

                // 第二步：生成beanName
                // 注意：这里需要先加载类才能获取注解信息
                // 简化实现：如果还没有类名，使用默认生成方式
                String beanName = generateBeanName(beanClassName);
                log.debug("为类 {} 生成Bean名称: {}", beanClassName, beanName);

                // 第三步：后置处理BeanDefinition（设置默认值）


                // 第四步：处理通用注解（@Lazy、@Primary、@Description等）并赋值给BeanDefinition对应的属性
                if (candidate instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
                }

                // 第五步：检查候选Bean是否可注册（处理Bean定义冲突）
                // 检查beanName是否已存在 -> 如果已存在，是否允许覆盖 -> 处理重复Bean定义的冲突
                if (checkCandidate(beanName, candidate)) {
                    // 第六步：注册BeanDefinition到容器
                    registry.registerBeanDefinition(beanName, candidate);
                    log.debug("注册BeanDefinition: {} -> {}", beanName, beanClassName);
                }
            }
        }

        log.debug("组件扫描完成");
    }

    /**
     * 检查候选Bean是否可注册 - 对应Spring的checkCandidate方法
     * 处理Bean名称冲突和覆盖策略
     */
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        log.debug("检查候选Bean是否可注册，Bean名称: {}", beanName);

        // 检查是否已存在同名的BeanDefinition
        if (registry.containsBeanDefinition(beanName)) {
            BeanDefinition existingDef = registry.getBeanDefinition(beanName);

            // 判断是否允许覆盖
            if (isAllowBeanDefinitionOverriding()) {
                log.warn("覆盖已存在的Bean定义，Bean名称: {}，旧定义: {}，新定义: {}",
                        beanName, existingDef, beanDefinition);
                return true;
            } else {
                // 不允许覆盖，抛出异常
                log.error("发现重复的Bean定义，Bean名称: {}", beanName);
                throw new RuntimeException(
                        "Duplicate bean definition for name '" + beanName + "'");
            }
        }

        // 检查是否在其他注册级别存在（如父容器），这里简化实现
        // Spring原版会检查父工厂等，我们暂时不实现

        log.debug("Bean名称 {} 可用", beanName);
        return true;
    }

    /**
     * 是否允许BeanDefinition覆盖
     * 对应Spring的allowBeanDefinitionOverriding属性
     */
    private boolean isAllowBeanDefinitionOverriding() {
        // 默认允许覆盖，可以根据配置调整
        // 在实际Spring中，这是ConfigurableBeanFactory的属性
        return true;
    }



    /**
     * BeanName生成器，简化实现：类名首字母小写
     */
    private String generateBeanName(String fullClassName) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            throw new IllegalArgumentException("fullClassName不能为null");
        }

        // 1. 提取类名
        int lastDotIndex = fullClassName.lastIndexOf('.');
        String className = lastDotIndex == -1 ?
                fullClassName :
                fullClassName.substring(lastDotIndex + 1);

        // 2. 转换为首字母小写
        if (className.isEmpty()) {
            return "";
        }

        // 首字母小写，其他保持不变
        return Character.toLowerCase(className.charAt(0)) +
                (className.length() > 1 ? className.substring(1) : "");
    }

    private int getBeanDefinitionCount() {
        // 需要registry提供获取BeanDefinition数量的方法
        return registry.getBeanDefinitionCount(); // 简化实现
    }

}
