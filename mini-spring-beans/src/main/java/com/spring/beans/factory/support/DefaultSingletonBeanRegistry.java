package com.spring.beans.factory.support;

import com.spring.beans.factory.ObjectFactory;
import com.spring.beans.factory.config.SingletonBeanRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: DefaultSingletonBeanRegistry
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/29 - 0:05
 * @version: v1.0
 */
@Slf4j
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private static final Object NULL_OBJECT = new Object();

    /** 一级缓存：完整的单例Bean */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /** 二级缓存：早期的单例Bean（已实例化但未完成属性注入） */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    /** 三级缓存：单例工厂（用于创建早期引用） */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /** 正在创建中的Bean名称 */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        // 简化实现：直接放入单例缓存
        this.singletonObjects.put(beanName, singletonObject);
        log.debug("注册单例Bean: {} -> {}", beanName, singletonObject.getClass().getSimpleName());
    }

    @Override
    public Object getSingleton(String beanName) {
        log.debug("获取单例Bean: {}", beanName);

        // 1. 从一级缓存获取完整的单例Bean
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            // 如果Bean正在创建中（可能发生循环依赖）
            synchronized (this.singletonObjects) {
                // 2. 从二级缓存获取早期Bean
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    // 3. 从三级缓存获取单例工厂
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        // 通过工厂创建早期引用
                        singletonObject = singletonFactory.getObject();
                        log.debug("通过单例工厂创建早期引用: {}", beanName);

                        // 将早期引用升级到二级缓存
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        // 移除三级缓存中的工厂
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }

        // 处理NULL对象占位符
        if (singletonObject == NULL_OBJECT) {
            return null;
        }

        return singletonObject;
    }

    /**
     * 检查Bean是否正在创建中
     */
    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * 添加单例工厂到三级缓存
     * 在Bean实例化后、属性注入前调用
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                log.debug("添加单例工厂到三级缓存: {}", beanName);
            }
        }
    }

    /**
     * 注册完整的单例Bean到一级缓存
     * 在Bean完全初始化后调用
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, (singletonObject != null ? singletonObject : NULL_OBJECT));
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            log.debug("注册完整单例Bean到一级缓存: {}", beanName);
        }
    }

    /**
     * 标记Bean开始创建
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new IllegalStateException("循环依赖检测: Bean '" + beanName + "' 已经在创建中");
        }
        log.debug("开始创建单例Bean: {}", beanName);
    }

    /**
     * 标记Bean创建完成
     */
    protected void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("单例Bean '" + beanName + "' 不在创建状态");
        }
        log.debug("完成创建单例Bean: {}", beanName);
    }
}
