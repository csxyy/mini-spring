package com.spring.context.support;

import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import com.spring.context.ApplicationContextAware;
import com.spring.context.ApplicationEventPublisher;
import com.spring.context.ApplicationEventPublisherAware;
import com.spring.context.EnvironmentAware;
import com.spring.context.weaving.ApplicationContext;
import com.spring.context.weaving.ConfigurableApplicationContext;
import com.spring.core.env.ConfigurableEnvironment;
import com.spring.core.env.StandardEnvironment;
import com.spring.core.io.DefaultResourceLoader;
import com.spring.core.io.Resource;
import com.spring.core.io.support.PathMatchingResourcePatternResolver;
import com.spring.core.io.support.ResourcePatternResolver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AbstractApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:37
 * @version: v1.0
 */
@Slf4j
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    /** 环境对象（懒加载） */
    private ConfigurableEnvironment environment;

    /** 此上下文启动时的系统时间（以毫秒为单位） */
    private long startupDate;

    /** 指示此上下文当前是否处于活动状态的标志 */
    private boolean active = false;

    /** 指示此上下文是否已关闭的标志 */
    private boolean closed = false;

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    private final ResourcePatternResolver resourcePatternResolver;

    public AbstractApplicationContext() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(this);
    }

    @Override
    public void refresh() {
        // 先忽略：synchronized, try-catch, StartupStep等优化细节

        log.info("====================开始刷新Spring应用上下文====================");

        // 1. 准备刷新上下文（简化：只设置基础状态）
        prepareRefresh();

        // 2. 获取BeanFactory（核心：就是之前创建的DefaultListableBeanFactory）
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // 3. 准备BeanFactory（核心：设置类加载器、添加后置处理器、添加忽略的接口等）
        prepareBeanFactory(beanFactory);

        // 4. 后置处理BeanFactory（空方法，留给子类扩展）
        postProcessBeanFactory(beanFactory);

        // 5. ⭐调用BeanFactory后置处理器（最核心：配置类解析在这里）
        invokeBeanFactoryPostProcessors(beanFactory);

        // 6. 注册Bean后置处理器（核心：准备Bean增强）
//        registerBeanPostProcessors(beanFactory);

        // 7. 初始化消息源（可忽略）
//        initMessageSource();

        // 8. 初始化事件广播器（可忽略）
//        initApplicationEventMulticaster();

        // 9. 模板方法，子类可以初始化特殊Bean（空实现，保持结构）
        onRefresh();

        // 10. 注册监听器（可忽略）
//        registerListeners();

        // 11. ⭐完成BeanFactory初始化（核心：实例化所有单例Bean）
        finishBeanFactoryInitialization(beanFactory);

        // 12. 完成刷新，发布上下文刷新事件（简化：只发布事件）
//        finishRefresh();

        log.info("====================Spring应用上下文刷新完成====================");
    }

    protected void prepareRefresh() {
        // 1. 记录启动时间，设置状态（核心逻辑保留）
        this.startupDate = System.currentTimeMillis();
        this.closed = false;
        this.active = true;
        log.debug("应用上下文启动时间: {}", this.startupDate);

        // 2. 初始化属性源（简化：空实现，保持扩展点）
        initPropertySources();

        // 3. 验证必需属性（简化：空实现，保持结构）
        getEnvironment().validateRequiredProperties();

        // 4. 准备早期事件监听器（简化：忽略事件相关逻辑）
        // Spring这里会处理earlyApplicationListeners，我们先忽略

        log.info("应用上下文准备完成");
    }

    /**
     * 初始化属性源（模板方法，子类可以覆盖）
     * Spring中用于初始化属性源，如系统属性、环境变量等
     */
    protected void initPropertySources() {
        // 简化：空实现，但保留这个重要的扩展点
        // 在Web环境中，Spring会在这里初始化ServletContext相关的属性源
        log.debug("初始化属性源（Mini版为空实现）");
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
            log.debug("创建并初始化环境对象：{}", this.environment.getClass().getSimpleName());
        }
        return this.environment;
    }

    /**
     * 创建环境对象 - 核心模板方法
     * Spring中不同的应用上下文会创建不同的环境：
     *  - 普通应用: StandardEnvironment
     *  - Web应用: StandardServletEnvironment
     *  - 响应式应用: StandardReactiveWebEnvironment
     * 设计思想：
     *  1. 懒加载：只有真正需要时才创建环境对象
     *  2. 模板方法：子类可以通过覆盖此方法提供特定环境
     *  3. 单一职责：环境创建与环境使用分离
     */
    protected ConfigurableEnvironment createEnvironment() {
        // 创建标志环境（包含系统属性、环境变量、配置文件）
        StandardEnvironment env = new StandardEnvironment();
        log.info("创建标准环境对象，包含属性源：{}", env.getPropertySourceNames());
        return env;
    }

    /**
     * 设置自定义环境（Spring中也有的方法）
     */
    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 获取新的 BeanFactory（模板方法）
     * Spring 设计思想：
     * 1. 确保 BeanFactory 只刷新一次
     * 2. 返回可配置的 BeanFactory 实例
     * 3. 为后续的 BeanFactory 配置做准备
     */
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        // 防止重复刷新 和 设置序列化ID
        refreshBeanFactory();
        return getBeanFactory();	// 返回BeanFactory实例
    }

    /**
     * 刷新BeanFactory - 模板方法，由子类实现具体的刷新逻辑
     * 作用：准备或重置BeanFactory的状态，确保其处于可用的刷新状态
     */
    protected abstract void refreshBeanFactory();

    /**
     * 获取BeanFactory实例 - 模板方法，由子类返回具体的BeanFactory
     * 作用：返回当前活动的、已刷新的BeanFactory实例
     */
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * 准备BeanFactory - 配置BeanFactory的基础设施
     * 作用：为BeanFactory设置各种解析器、处理器，注册特殊Bean等
     */
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        log.info("开始准备BeanFactory");

        // 1. 设置类加载器
        beanFactory.setBeanClassLoader(getClassLoader());
        log.debug("设置BeanFactory类加载器: {}", getClassLoader());

        // 2. 添加ApplicationContextAware处理器（核心）
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        log.debug("添加ApplicationContextAware处理器");

        // 3. 设置忽略的依赖接口（这些接口由容器自动注入，不通过自动装配）
        // 作用：标记这些接口不由自动装配处理，而是由容器特殊处理
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        log.debug("设置忽略的依赖接口: EnvironmentAware, ResourceLoaderAware, ApplicationEventPublisherAware, ApplicationContextAware");

        // 4. 注册可解析的依赖（这些依赖可以直接从BeanFactory获取）
        // 作用：注册一些特殊类型的依赖，当Bean需要这些类型时直接从容器获取
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
        log.debug("注册可解析的依赖: BeanFactory, ResourceLoader, ApplicationEventPublisher, ApplicationContext");

        // 5. 注册环境相关的单例Bean
        // 注册环境Bean
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
            log.debug("注册环境单例Bean: {}", ENVIRONMENT_BEAN_NAME);
        }

        // 注册系统属性Bean
//        if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
//            beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME,
//                    getEnvironment().getSystemProperties());
//            log.debug("注册系统属性单例Bean: {}", SYSTEM_PROPERTIES_BEAN_NAME);
//        }

        // 注册系统环境变量Bean
//        if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
//            beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME,
//                    getEnvironment().getSystemEnvironment());
//            log.debug("注册系统环境变量单例Bean: {}", SYSTEM_ENVIRONMENT_BEAN_NAME);
//        }

        log.info("BeanFactory准备完成");
    }

    /**
     * 后置处理BeanFactory - 模板方法
     * @param beanFactory
     */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    /**
     * ⭐调用BeanFactory后置处理器
     */
    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 委托给PostProcessorRegistrationDelegate执行所有BeanFactoryPostProcessor
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        // 获取程序员自己 ioc.addBeanFactoryPostProcessor(xxx); 容器刷新前 （很少这么用）
        return this.beanFactoryPostProcessors;
    }

    protected void onRefresh() {
        // 对于子类：默认情况下不执行任何作。
    }

    /**
     * 核心：实例化所有单例Bean
     */
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        // 1. 设置必要的服务
//        setupEssentialServices(beanFactory);

        // 2. 冻结配置
        beanFactory.freezeConfiguration();

        // 3. 实例化单例Bean
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public boolean containsLocalBean(String name) {
        return getBeanFactory().containsLocalBean(name);
    }

    @Override
    public Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public boolean isSingleton(String name) {
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }

    @Override
    public void publishEvent(Object event) {

    }


    // ===================== Lifecycle：生命周期管理 =========================

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return true;
    }


    // ===================== ResourcePatternResolver：扩展资源加载 =========================

    @Override
    public Resource[] getResources(String locationPattern) {
        return this.resourcePatternResolver.getResources(locationPattern);
    }
}
