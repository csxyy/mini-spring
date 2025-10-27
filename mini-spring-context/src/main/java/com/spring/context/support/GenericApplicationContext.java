package com.spring.context.support;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.DefaultListableBeanFactory;

/**
 * ClassName: GenericApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:46
 * @version: v1.0
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
    private final DefaultListableBeanFactory beanFactory;

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
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
}
