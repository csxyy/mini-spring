package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * ClassName: RootBeanDefinition
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 0:48
 * @version: v1.0
 */
@Slf4j
public class RootBeanDefinition extends AbstractBeanDefinition {

    volatile Method factoryMethodToIntrospect; // 被解析后的工厂方法（@Bean）
    public boolean isFactoryMethodUnique; // 是工厂方法唯一的（存在）

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
     * 检查验证BeanDefinition是否有效 - 最简化版本
     */
    public void validate() {
        log.debug("验证BeanDefinition有效性");

        // 情况1：普通Bean（通过类创建）
        boolean hasBeanClass = (getBeanClass() != null || getBeanClassName() != null);

        // 情况2：工厂方法创建
        boolean hasFactoryMethod = (getFactoryMethodName() != null);

        // 情况3：工厂Bean创建
        boolean hasFactoryBean = (getFactoryBeanName() != null);

        // 基本验证：至少有一种有效的方式创建Bean
        if (hasBeanClass) {
            // 普通Bean，不需要额外验证
            log.debug("普通Bean验证通过");
            return;
        }

        if (hasFactoryMethod) {
            // 工厂方法Bean
            if (hasFactoryBean) {
                // 实例工厂方法：factoryBean + factoryMethod
                log.debug("实例工厂方法Bean验证通过");
                return;
            }

            // 静态工厂方法：必须有类名作为factoryClass
            if (getBeanClassName() != null) {
                log.debug("静态工厂方法Bean验证通过");
                return;
            }
        }

        // 如果没有任何有效的创建方式，抛出异常
        throw new IllegalArgumentException("无效的BeanDefinition：无法确定如何创建Bean实例。\n" +
                "beanClass=" + getBeanClass() +
                ", beanClassName=" + getBeanClassName() +
                ", factoryBeanName=" + getFactoryBeanName() +
                ", factoryMethodName=" + getFactoryMethodName());
    }

    public boolean isFactoryMethod(Method candidate) {
        // 简单比较：候选方法名是否等于保存的工厂方法名
        return candidate.getName().equals(getFactoryMethodName());
    }

    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    public void setResolvedFactoryMethod(Method method) {
        this.factoryMethodToIntrospect = method;
        if (method != null) {
            setFactoryMethodName(method.getName());
            this.isFactoryMethodUnique = true;
        }
    }

    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());

        // 使用具体类名作为前缀
        sb.append(": ");

        // 基本信息
        sb.append("class=").append(getBeanClass() != null ?
                getBeanClass().getSimpleName() : getBeanClassName());

        // 工厂信息
        if (getFactoryBeanName() != null) {
            sb.append(", factoryBean=").append(getFactoryBeanName());
        }
        if (getFactoryMethodName() != null) {
            sb.append(", factoryMethod=").append(getFactoryMethodName());
        }

        // 作用域
        if (!SCOPE_SINGLETON.equals(getScope())) {
            sb.append(", scope=").append(getScope());
        }

        // 懒加载
        if (isLazyInit()) {
            sb.append(", lazyInit=true");
        }

        return sb.toString();
    }
}
