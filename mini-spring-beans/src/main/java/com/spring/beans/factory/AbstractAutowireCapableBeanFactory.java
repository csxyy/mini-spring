package com.spring.beans.factory;

import com.spring.beans.BeanWrapper;
import com.spring.beans.BeanWrapperImpl;
import com.spring.beans.factory.config.AutowireCapableBeanFactory;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.AbstractBeanFactory;
import com.spring.beans.factory.support.RootBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * ClassName: AbstractAutowireCapableBeanFactory
 * Description: 抽象自动装配BeanFactory - 实现Bean创建的核心逻辑
 *
 * @Author: csx
 * @Create: 2025/10/28 - 15:00
 * @version: v1.0
 */
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
                                        implements AutowireCapableBeanFactory {


    /**
     * 创建Bean实例的核心入口
     * @param beanName Bean名称
     * @param mbd Bean定义
     * @param args 构造方法参数
     * @return Bean实例
     */
    @Override	//args 表示构造方法参数
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args){
        log.debug("开始创建Bean: {}", beanName);

        // 1. 解析Bean类
        Class<?> beanClass = mbd.getBeanClass();
        if (beanClass == null) {
            throw new RuntimeException("无法解析Bean类：" + beanClass);
        }

        // 2. 给BeanPostProcessor机会返回代理对象（AOP前置处理）
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanName, mbd);
        if (bean != null) {
            log.debug("BeanPostProcessor返回了代理对象: {}", beanName);
            return bean;
        }

        // 3. 实际创建Bean实例
        return doCreateBean(beanName, mbd, args);
    }

    /**
     * Bean实例化前的后置处理（用于AOP等）
     */
    protected Object applyBeanPostProcessorsBeforeInstantiation(String beanName, BeanDefinition mbd) {
        // 简化实现：目前没有BeanPostProcessor，直接返回null
        // 后续实现AOP时会在这里创建代理对象
        return null;
    }

    /**
     * 实际创建Bean实例
     */
    protected Object doCreateBean(String beanName, RootBeanDefinition mbd, Object[] args) {
        log.debug("开始实际创建Bean: {}", beanName);

        // 1. 实例化Bean（创建对象实例）
        BeanWrapper instanceWrapper = createBeanInstance(beanName, mbd, args);
        Object beanInstance = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();

        log.debug("Bean实例化完成: {} -> {}", beanName, beanType.getSimpleName());

        // 2. 将早期引用添加到三级缓存（解决循环依赖）
//        if (mbd.isSingleton() && isSingletonCurrentlyInCreation(beanName)) {
//            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, beanInstance));
//        }

        // 3. 属性注入（依赖注入）
//        populateBean(beanName, mbd, beanInstance);
        log.debug("Bean属性注入完成: {}", beanName);

        // 4. 初始化Bean
//        beanInstance = initializeBean(beanName, beanInstance, mbd);
        log.debug("Bean初始化完成: {}", beanName);

        return beanInstance;
    }

    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        log.debug("创建Bean实例: {}", beanName);
        Class<?> beanClass = resolveBeanClass(mbd, beanName);

        // 1. 工厂方法（反射调用@Bean定义的方法）
//        if (mbd.getFactoryMethodName() != null) {
//            return instantiateUsingFactoryMethod(beanName, mbd, args);
//        }

        // 2. 构造方法推断
//        Constructor<?>[] ctors = findAutowiredConstructors(beanClass);
//        if (ctors != null || !ObjectUtils.isEmpty(args)) {
//            return autowireConstructor(beanName, mbd, ctors, args);
//        }

        // 3. 无参构造
        return instantiateBean(beanName, mbd);
    }

    protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        try {
            Object beanInstance = mbd.getBeanClass().getDeclaredConstructor().newInstance();
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            return bw;
        } catch (Exception e) {
            throw new RuntimeException("Bean实例化失败：", e);
        }
    }
}
