package com.spring.beans.factory;

import com.spring.beans.BeanWrapper;
import com.spring.beans.BeanWrapperImpl;
import com.spring.beans.factory.config.AutowireCapableBeanFactory;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.AbstractBeanFactory;
import com.spring.beans.factory.support.RootBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
    @Override    //args 表示构造方法参数
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args){
        log.debug("开始创建Bean: {}", beanName);

        // 1. 解析Bean类 - 准备方法重写（处理@Lookup）- 占时不实现

        // 2. 给BeanPostProcessor机会返回代理对象
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

    /**
     * 推断构造、构造方法注入、@Bean注解处理都在这里
     */
    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        log.debug("创建Bean实例: {}", beanName);
        Class<?> beanClass = mbd.getBeanClass();

        // 1. 工厂方法（反射调用@Bean定义的方法）
        if (mbd.getFactoryMethodName() != null) {
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }

        // 2. 构造方法推断
//        Constructor<?>[] ctors = findAutowiredConstructors(beanClass);
//        if (ctors != null || !ObjectUtils.isEmpty(args)) {
//            return autowireConstructor(beanName, mbd, ctors, args);
//        }

        // 3. 无参构造
        return instantiateBean(beanName, mbd);
    }

    protected BeanWrapper instantiateUsingFactoryMethod(
            String beanName, RootBeanDefinition mbd, Object[] explicitArgs) {
        log.debug("使用工厂方法实例化Bean: {}，工厂方法: {}",
                beanName, mbd.getFactoryMethodName());

        // 静态方法：beanClass == 配置类全限定名 && factoryBeanName == null && factoryMethodName == 方法名
        // 普通方法：beanClass == null && factoryBeanName == myConfig && factoryMethodName == 方法名

        Object factoryBean;    // 就是myConfig
        Class<?> factoryClass; // myConfig的类型
        boolean isStatic;

        try {

            // factoryBeanName是myConfig，beanName是userService
            String factoryBeanName = mbd.getFactoryBeanName();
            if (factoryBeanName != null) {
                // 普通方法：实例工厂方法

                // 防止循环依赖：factoryBean不能是自己
                if (factoryBeanName.equals(beanName)) {
                    String errorMsg = String.format("工厂Bean名称[%s]不能与目标Bean名称[%s]相同，会导致循环依赖",
                            factoryBeanName, beanName);
                    log.error(errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }

                // 先获取或创建工厂Bean（myConfig）
                log.debug("获取工厂Bean: {}", factoryBeanName);
                factoryBean = this.getBean(factoryBeanName);

                // 检查单例缓存，防止重复创建
                if (mbd.isSingleton() && this.containsSingleton(beanName)) {
                    String errorMsg = String.format("单例Bean[%s]已经存在，无法重复创建", beanName);
                    log.error(errorMsg);
                    throw new IllegalStateException(errorMsg);
                }

                // beanName依赖了factoryBeanName，userService依赖了myConfig
                // 注意：这里可能有循环依赖，简化处理，先创建工厂Bean
                factoryClass = factoryBean.getClass();
                isStatic = false;

                log.debug("使用实例工厂方法，工厂Bean类型: {}", factoryClass.getName());
            } else {
                // 静态方法：静态工厂方法

                if (!mbd.hasBeanClass()) {
                    String errorMsg = String.format("静态工厂方法Bean[%s]没有指定工厂类", beanName);
                    log.error(errorMsg);
                    throw new IllegalStateException(errorMsg);
                }

                factoryBean = null;
                factoryClass = mbd.getBeanClass();
                isStatic = true;

                log.debug("使用静态工厂方法，工厂类: {}", factoryClass.getName());
            }

            Method factoryMethodToUse = null; // factory-method工厂方法
            Object[] argsToUse = null;  // 参数

            // 先看有没有显示传入的参数
            if (explicitArgs != null) {
                argsToUse = explicitArgs;
                log.debug("使用显式参数: {}", Arrays.toString(explicitArgs));
            } else {
                // 尝试从mbd的缓存中获取已解析的工厂方法和参数
                // 这里我们暂时不实现缓存，直接查找方法
                log.debug("没有显式参数，将使用默认参数或无参方法");
            }

            // 查找合适的工厂方法
            // 注：上面如果从缓存中拿到的话就不用处理了
            if (factoryMethodToUse == null) {
                String factoryMethodName = mbd.getFactoryMethodName();
                if (factoryMethodName == null || factoryMethodName.isEmpty()) {
                    String errorMsg = String.format("Bean[%s]没有指定工厂方法名", beanName);
                    log.error(errorMsg);
                    throw new IllegalArgumentException(errorMsg);
                }

                // 简化实现：占时只查找无参方法
                try {
                    log.debug("查找工厂方法: {}.{}()",
                            factoryClass.getName(), factoryMethodName);

                    factoryMethodToUse = factoryClass.getDeclaredMethod(factoryMethodName);
                    factoryMethodToUse.setAccessible(true);

                    log.debug("找到工厂方法: {}，返回类型: {}",
                            factoryMethodToUse, factoryMethodToUse.getReturnType());

                } catch (NoSuchMethodException e) {
                    String errorMsg = String.format("在类[%s]中找不到工厂方法[%s]",
                            factoryClass.getName(), factoryMethodName);
                    log.error(errorMsg, e);
                    throw new IllegalArgumentException(errorMsg, e);
                }
            }

            return new BeanWrapperImpl(factoryMethodToUse.invoke(factoryBean, explicitArgs));
        } catch (Exception e) {
            throw new RuntimeException("@Bean方法实例化Bean失败: " + beanName, e);
        }
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
