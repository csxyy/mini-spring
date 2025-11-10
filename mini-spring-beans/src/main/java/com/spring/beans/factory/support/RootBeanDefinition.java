package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;

/**
 * ClassName: RootBeanDefinition
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 0:48
 * @version: v1.0
 */
public class RootBeanDefinition extends AbstractBeanDefinition {

    public RootBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }

    public RootBeanDefinition(Class<?> beanClass, String scope, boolean lazyInit) {
        setBeanClass(beanClass);
        setScope(scope);
        setLazyInit(lazyInit);
    }

    /**
     * 检查验证RootBeanDefinition是否有效
     */
    public void validate() {
        // 简化验证逻辑
        if (getBeanClass() == null) {
            throw new IllegalArgumentException("Bean 类不得为 null");
        }
    }
}
