package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.core.io.Resource;
import com.spring.core.io.support.PathMatchingResourcePatternResolver;
import com.spring.core.io.support.ResourcePatternResolver;
import com.spring.core.type.classreading.MetadataReader;
import com.spring.core.type.classreading.MetadataReaderFactory;
import com.spring.core.type.classreading.SimpleMetadataReaderFactory;
import com.spring.stereotype.Component;
import com.spring.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName: ClassPathScanningCandidateComponentProvider
 * Description:
 *
 * 类路径扫描候选组件提供者 - 对应Spring的ClassPathScanningCandidateComponentProvider
 * 负责扫描类路径下的候选组件
 *
 * @Author: csx
 * @Create: 2025/12/2 - 22:32
 * @version: v1.0
 */
@Slf4j
public class ClassPathScanningCandidateComponentProvider {

    private String resourcePattern = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    /** 包含过滤器列表 */
    private final List<Class<? extends Annotation>> includeFilters = new ArrayList<>();

    /** 排除过滤器列表 */
    private final List<Class<? extends Annotation>> excludeFilters = new ArrayList<>();

    /**
     * 添加包含过滤器
     */
    public void addIncludeFilter(Class<? extends Annotation> includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    /**
     * 添加排除过滤器
     */
    public void addExcludeFilter(Class<? extends Annotation> excludeFilter) {
        this.excludeFilters.add(excludeFilter);
    }

    /**
     * 注册默认过滤器
     */
    public void registerDefaultFilters() {
        this.includeFilters.clear();
        this.includeFilters.add(Component.class);
        log.debug("注册默认包含过滤器: @Component");
    }

    /**
     * 扫描基础包下的所有候选组件 - 对应Spring的findCandidateComponents->scanCandidateComponents方法
     *
     * Spring原版逻辑：
     * 1. 将包名转换为搜索路径
     * 2. 获取所有匹配的资源
     * 3. 遍历资源，将符合条件的类转换为BeanDefinition
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        log.debug("开始扫描基础包下的候选组件: {}", basePackage);

        Set<BeanDefinition> candidates = new LinkedHashSet<>();

        try {
            // 第一步：构建类路径搜索模式，将包名转换为类路径搜索模式
            // 如：com.it -> classpath*:com/it/**/*.class
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + this.resourcePattern;

            log.debug("构建的搜索路径: {}", packageSearchPath);

            // 第二步：获取所有.class文件资源（封装到Resource对象里）
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            log.debug("找到 {} 个资源文件", resources.length);

            // 第三步：遍历所有资源文件（这一步我们暂时只记录日志，后续实现转换逻辑）
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                log.debug("处理资源文件: {}", filename);

                // 过滤1：忽略CGLIB生成的代理类
                if (filename != null && filename.contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
                    log.debug("跳过CGLIB代理类: {}", filename);
                    continue;
                }

                try {
                    // 第四步：使用ASM技术解析类元数据
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);

                    // 第五步：应用过滤器判断是否为候选组件
                    if (isCandidateComponent(metadataReader)) {
                        log.debug("类 {} 通过过滤器，创建BeanDefinition", metadataReader.getClassMetadata().getClassName());

                        // 第六步：创建BeanDefinition
                        // 注意：我们没有定义ScannedGenericBeanDefinition，使用AnnotatedGenericBeanDefinition
                        // 但需要传递元数据，所以我们需要扩展AnnotatedGenericBeanDefinition
                        // 直接使用MetadataReader构造BeanDefinition
                        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(metadataReader);
                        abd.setSource(resource);

                        // 第七步：二次验证：检查类是否可实例化
                        // 不能是接口或抽象类，如果是抽象类，但是有@Lookup注解的方法则通过（@Lookup占时不实现）
                        if (isCandidateComponent(abd)) {
                            candidates.add(abd);
                            log.debug("添加候选BeanDefinition: {}", abd.getBeanClassName());
                        } else {
                            log.debug("类 {} 未通过二次验证", metadataReader.getClassMetadata().getClassName());
                        }
                    } else {
                        log.debug("类 {} 未通过过滤器", metadataReader.getClassMetadata().getClassName());
                    }
                } catch (IOException e) {
                    log.warn("无法读取资源元数据: {}", resource.getDescription(), e);
                } catch (Exception e) {
                    log.warn("处理资源时发生异常: {}", resource.getDescription(), e);
                }
            }
        } catch (Exception e) {
            log.error("扫描候选组件时发生异常，基础包: {}", basePackage, e);
            throw new RuntimeException("Failed to scan candidate components from base package: " + basePackage, e);
        }

        log.debug("扫描完成，找到 {} 个候选组件", candidates.size());
        return candidates;
    }

    /**
     * 基于元数据判断是否为候选组件 - 对应Spring的isCandidateComponent(MetadataReader)
     * 这里应用includeFilters和excludeFilters
     */
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        // 首先检查排除过滤器
        for (Class<? extends Annotation> excludeFilter : excludeFilters) {
            if (metadataReader.getAnnotationMetadata().isAnnotated(excludeFilter.getName())) {
                log.debug("类 {} 有排除注解 {}，跳过",
                        metadataReader.getClassMetadata().getClassName(), excludeFilter.getName());
                return false;
            }
        }

        // 检查包含过滤器
        for (Class<? extends Annotation> includeFilter : includeFilters) {
            if (metadataReader.getAnnotationMetadata().isAnnotated(includeFilter.getName())) {
                log.debug("类 {} 有包含注解 {}，通过",
                        metadataReader.getClassMetadata().getClassName(), includeFilter.getName());
                return true;
            }
        }

        // 如果没有包含过滤器匹配，返回false
        return false;
    }

    /**
     * 二次验证：检查BeanDefinition是否为候选组件 - 对应Spring的isCandidateComponent(BeanDefinition)
     * 主要检查类是否可实例化（不是接口，不是抽象类等）
     */
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        log.debug("二次验证BeanDefinition: {}", className);

        // 检查是否是具体类（非接口且非抽象）
        if (beanDefinition.getMetadata().isInterface() || beanDefinition.getMetadata().isAbstract()) {
            log.debug("类 {} 是接口或抽象类，跳过", beanDefinition.getBeanClassName());
            return false;
        }

        // 检查是否有合适的构造方法
        // 这里需要加载类来检查构造方法，但我们可以延迟到真正需要时
        // 简化：假设有默认构造方法，或者标记为延迟检查
        log.debug("类 {} 通过二次验证（构造方法检查延迟到实例化时）", className);

        // 在实际Spring中，这里还会检查@Lookup注解等，我们暂时不实现

        return true;
    }

    /**
     * 解析基础包路径
     * 将包名中的点转换为路径分隔符
     */
    protected String resolveBasePackage(String basePackage) {
        log.debug("解析基础包路径: {}", basePackage);
        // 将包名中的点替换为路径分隔符
        String resolved = basePackage.replace('.', '/');
        log.debug("解析后的路径: {}", resolved);
        return resolved;
    }

    /**
     * 获取资源模式解析器
     */
    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    /**
     * 设置资源模式解析器
     */
    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 设置资源匹配模式
     */
    public void setResourcePattern(String resourcePattern) {
        this.resourcePattern = resourcePattern;
    }

    /**
     * 获取元数据读取器工厂
     */
    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new SimpleMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }
}
