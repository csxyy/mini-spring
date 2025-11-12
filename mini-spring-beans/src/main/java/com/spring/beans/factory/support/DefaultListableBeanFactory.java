package com.spring.beans.factory.support;

import com.spring.beans.factory.AbstractAutowireCapableBeanFactory;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: DefaultListableBeanFactory
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:48
 * @version: v1.0
 */
@Slf4j
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
                                implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    // 核心存储
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final List<String> beanDefinitionNames = new ArrayList<>();

    /** 在依赖项检查和自动连接时要忽略的依赖类型 */
    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();

    /** 从依赖类型映射到相应的自动连接值 */
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

    private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

    /** 配置冻结 */
    private boolean configurationFrozen = false;

    // 是否允许覆盖（简化：默认允许）
    private boolean allowBeanDefinitionOverriding = true;

    // ============ BeanDefinitionRegistry 接口实现 ============

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // 1. 基础校验
        if (beanName == null || beanName.isEmpty()) {
            throw new IllegalArgumentException("Bean 名称不得为 null");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("BeanDefinition 不得为 null");
        }

        // 2. 检查配置是否已冻结
        if (this.configurationFrozen) {
            throw new IllegalStateException("BeanFactory配置已冻结，无法注册新的Bean定义: " + beanName);
        }

        // 3. 检查是否已存在
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);

        // 4. 处理覆盖逻辑
        if (existingDefinition != null) {
            if (!allowBeanDefinitionOverriding) {
                throw new IllegalStateException("无法覆盖名称的 Bean 定义: " + beanName);
            }
            log.info("覆盖Bean定义: {}", beanName);
        } else {
            // 新注册的Bean，添加到名称列表
            this.beanDefinitionNames.add(beanName);
        }

        // 5. 注册到Map
        this.beanDefinitionMap.put(beanName, beanDefinition);

        // 6. 清除相关缓存（简化版）
        clearBeanDefinitionCache(beanName);

        log.info("注册Bean定义: {} -> {}", beanName, beanDefinition.getBeanClass().getSimpleName());
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        if (!this.beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalArgumentException("未找到Bean定义: " + beanName);
        }
        this.beanDefinitionMap.remove(beanName);
        this.beanDefinitionNames.remove(beanName);
        log.info("移除Bean定义: {}", beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("未找到Bean定义: " + beanName);
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionNames.toArray(new String[0]);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    private void clearBeanDefinitionCache(String beanName) {
        // 简化版：后续实现合并BeanDefinition缓存时再完善
        // 目前先留空或简单日志
        log.info("清除Bean定义缓存: {}", beanName);
    }


    // ============ ListableBeanFactory 接口实现 ============

    /**
     * 根据类型获取Bean
     * @param requiredType Bean类型
     * @return
     * @param <T>
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        // 后续实现按类型获取Bean的逻辑
        log.debug("按类型获取Bean: {}", requiredType.getSimpleName());
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        log.debug("查找类型为 {} 的Bean, includeNonSingletons: {}, allowEagerInit: {}",
                type.getSimpleName(), includeNonSingletons, allowEagerInit);

        List<String> result = new ArrayList<>();

        // 1. 遍历所有Bean定义名称
        for (String beanName : getBeanDefinitionNames()) {
            // 2. 检查是否为单例（如果不包含非单例且当前Bean不是单例，则跳过）
            if (!includeNonSingletons && !isSingleton(beanName)) {
                continue;
            }

            // 3. 检查类型是否匹配
            if (isTypeMatch(beanName, type)) {
                result.add(beanName);
            }
        }

        // 4. 同时检查手动注册的单例（通过registerSingleton注册的）
        if (includeNonSingletons) {
            String[] singletonNames = getSingletonNames();
            for (String singletonName : singletonNames) {
                if (!result.contains(singletonName) && isTypeMatch(singletonName, type)) {
                    result.add(singletonName);
                }
            }
        }

        log.debug("找到 {} 个类型为 {} 的Bean: {}", result.size(), type.getSimpleName(), result);
        return result.toArray(new String[0]);
    }

    // ============ ConfigurableBeanFactory 接口实现 ============

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        log.debug("设置Bean类加载器: {}", beanClassLoader);
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }


    // ============ ConfigurableListableBeanFactory 接口实现 ============

    @Override
    public void setSerializationId(String serializationId) {
        log.debug("设置序列化ID: {}", serializationId);
        // 简化实现，记录日志即可
    }

    @Override
    public void freezeConfiguration() {
        this.configurationFrozen = true;
        log.info("BeanFactory配置已冻结");
    }

    @Override
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    @Override
    public void ignoreDependencyInterface(Class<?> type) {
        this.ignoredDependencyInterfaces.add(type);
        log.debug("忽略依赖类型: {}", type.getSimpleName());
    }

    public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {
        this.resolvableDependencies.put(dependencyType, autowiredValue);
        log.debug("注册可解析依赖: {} -> {}", dependencyType.getSimpleName(), autowiredValue.getClass().getSimpleName());
    }

    @Override
    public void preInstantiateSingletons() {
        // 1.创建Bean名称副本
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

        // 2.遍历所有Bean定义
        for (String beanName : beanNames) {
            BeanDefinition mbd = getBeanDefinition(beanName);

            // 检查条件
            if (!mbd.isAbstract() && mbd.isSingleton()) {
                preInstantiateSingleton(beanName, mbd);
            }
        }

        // 3.执行回调（可选）
/*        for (String beanName : beanNames) {
            Object bean = getSingleton(beanName);
            if (bean instanceof SmartInitializingSingleton) {
                ((SmartInitializingSingleton) bean).afterSingletonsInstantiated();
            }
        }*/
    }

    private void preInstantiateSingleton(String beanName, BeanDefinition mbd) {
        // 占时：只实现同步创建
        if (!mbd.isLazyInit()) {
            // 同步创建Bean
            instantiateSingleton(beanName);
        }
    }

    private void instantiateSingleton(String beanName) {
        // 根据beanName判断是不是FactoryBean，会根据beanName找到BeanDefinition，从而找到对应类型，从而进行判断
        if (isFactoryBean(beanName)) {
            // 创建FactoryBean本身，先创建MyFactoryBean对象
            Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);

            // 创建FactoryBean中getObject()方法返回的Bean
//            if (bean instanceof SmartFactoryBean<?> smartFactoryBean && smartFactoryBean.isEagerInit()) {
//                // 调用MyFactoryBean对象的getObject()
//                getBean(beanName);
//            }
        }
        else {
            getBean(beanName);
        }
    }

    // ============ 配置方法 ============

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

}
