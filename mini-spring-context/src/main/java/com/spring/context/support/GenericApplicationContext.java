package com.spring.context.support;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.DefaultListableBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClassName: GenericApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:46
 * @version: v1.0
 */
@Slf4j
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
    private final DefaultListableBeanFactory beanFactory;

    /**  标记容器是否已刷新 */
    private final AtomicBoolean refreshed = new AtomicBoolean(false);

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanFactory.containsBeanDefinition(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanFactory.getBeanDefinitionCount();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        beanFactory.removeBeanDefinition(beanName);
    }

    @Override	//AnnotationConfigApplicationContext会调它
    protected final void refreshBeanFactory() throws IllegalStateException {
        // 防止重复刷新：确保容器只被刷新一次
        if (!this.refreshed.compareAndSet(false, true)) {
            // 如果反复刷新容器（调refresh方法）就会进入抛异常
            throw new IllegalStateException(
                    "GenericApplicationContext 不支持多次刷新：只能调用一次 'refresh' 方法");
        }
        // 设置BeanFactory的序列化ID
        this.beanFactory.setSerializationId(getId());
    }

    /**
     * 获取BeanFactory实例 - 确保在BeanFactory刷新后才能访问
     * 1. 检查BeanFactory是否已刷新
     * 2. 返回已刷新的BeanFactory实例
     */
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        if (!this.refreshed.get()) {
            throw new IllegalStateException("BeanFactory 尚未刷新，请先调用 refresh() 方法");
        }
        log.debug("返回已刷新的BeanFactory实例");
        return this.beanFactory;
    }

    /**
     * 获取上下文ID - 用于序列化标识
     */
    public String getId() {
        return this.toString();
    }
}
