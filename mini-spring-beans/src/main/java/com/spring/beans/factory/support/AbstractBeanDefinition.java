package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;

/**
 * ClassName: AbstractBeanDefinition
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:27
 * @version: v1.0
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {
    private volatile Class<?> beanClass;

    private String scope = SCOPE_SINGLETON;

    private Boolean lazyInit = false;   // 懒加载

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean isLazyInit() {
        return lazyInit;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope) || scope == null;
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }
}
