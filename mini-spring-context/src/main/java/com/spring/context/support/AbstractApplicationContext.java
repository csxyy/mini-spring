package com.spring.context.support;

import com.spring.context.weaving.ConfigurableApplicationContext;
import com.spring.core.env.ConfigurableEnvironment;
import com.spring.core.env.StandardEnvironment;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: AbstractApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:37
 * @version: v1.0
 */
@Slf4j
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {
    /** 此上下文启动时的系统时间（以毫秒为单位） */
    private long startupDate;

    /** 指示此上下文当前是否处于活动状态的标志 */
    private boolean active = false;

    /** 指示此上下文是否已关闭的标志 */
    private boolean closed = false;

    /** 环境对象（懒加载） */
    private ConfigurableEnvironment environment;


    @Override
    public void refresh() {
        // 先忽略：synchronized, try-catch, StartupStep等优化细节

        log.info("====================开始刷新Spring应用上下文====================");

        // 1. 准备刷新上下文（简化：只设置基础状态）
        prepareRefresh();

        // 2. 获取BeanFactory（核心：就是之前创建的DefaultListableBeanFactory）
//        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // 3. 准备BeanFactory（核心：设置类加载器、添加后置处理器、添加忽略的接口等）
//        prepareBeanFactory(beanFactory);

        // 4. 后置处理BeanFactory（空方法，留给子类扩展）
//        postProcessBeanFactory(beanFactory);

        // 5. ⭐调用BeanFactory后置处理器（最核心：配置类解析在这里）
//        invokeBeanFactoryPostProcessors(beanFactory);

        // 6. 注册Bean后置处理器（核心：准备Bean增强）
//        registerBeanPostProcessors(beanFactory);

        // 7. 初始化消息源（可忽略）
//        initMessageSource();

        // 8. 初始化事件广播器（可忽略）
//        initApplicationEventMulticaster();

        // 9. 模板方法，子类可以初始化特殊Bean（空实现，保持结构）
//        onRefresh();

        // 10. 注册监听器（可忽略）
//        registerListeners();

        // 11. ⭐完成BeanFactory初始化（核心：实例化所有单例Bean）
//        finishBeanFactoryInitialization(beanFactory);

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

}
