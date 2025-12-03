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

    /**
     * 无参构造方法（用于基于元数据创建BeanDefinition）
     */
    public RootBeanDefinition() {
        // 什么都不做，beanClass为null
    }

    public RootBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
    }

    public RootBeanDefinition(Class<?> beanClass, String scope, boolean lazyInit) {
        setBeanClass(beanClass);
        setScope(scope);
        setLazyInit(lazyInit);
    }

    /**
     * 从另一个BeanDefinition复制的构造方法
     */
    public RootBeanDefinition(BeanDefinition original) {
        setBeanClass(original.getBeanClass());
        setScope(original.getScope());
        setLazyInit(original.isLazyInit());

        // 复制工厂方法相关属性
        if (original instanceof AbstractBeanDefinition abd) {
            setFactoryBeanName(abd.getFactoryBeanName());
            setFactoryMethodName(abd.getFactoryMethodName());
        }
    }

    /**
     * 设置Bean类名
     */
    public void setBeanClassName(String beanClassName) {
        setBeanClass(null); // 清空类引用
        // 将类名存储在父类的某个属性中，或者需要添加新属性
        // 这里我们可以在AbstractBeanDefinition中添加beanClassName属性
        super.setBeanClassName(beanClassName);
    }

    /**
     * 检查验证RootBeanDefinition是否有效
     */
    public void validate() {
        // 简化验证逻辑
        if (getBeanClass() == null) {
            throw new IllegalArgumentException("Bean 类不得为 null");
        }

        // 如果有工厂方法，验证工厂Bean名称和方法名称都存在
        if (getFactoryBeanName() != null && getFactoryMethodName() == null) {
            throw new IllegalArgumentException("设置了工厂Bean名称但未设置工厂方法名称");
        }
        if (getFactoryMethodName() != null && getFactoryBeanName() == null) {
            throw new IllegalArgumentException("设置了工厂方法名称但未设置工厂Bean名称");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RootBeanDefinition: ");
        sb.append("class=").append(getBeanClass().getSimpleName());

        if (getFactoryBeanName() != null) {
            sb.append(", factoryBean=").append(getFactoryBeanName());
        }
        if (getFactoryMethodName() != null) {
            sb.append(", factoryMethod=").append(getFactoryMethodName());
        }

        return sb.toString();
    }
}
