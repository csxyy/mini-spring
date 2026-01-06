package com.spring.beans.factory.config;

/**
 * ClassName: BeanDefinition
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:54
 * @version: v1.0
 */
public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";


    Class<?> getBeanClass();
    void setBeanClass(Class<?> beanClass);

    String getScope();
    void setScope(String scope);

    boolean isLazyInit();
    void setLazyInit(boolean lazyInit);

    boolean isSingleton();
    boolean isPrototype();

    boolean isAbstract();   // 非抽象的

    void setPrimary(boolean primary);
    boolean isPrimary();

    /**
     * 设置工厂Bean名称
     */
    void setFactoryBeanName(String factoryBeanName);
    String getFactoryBeanName();

    /**
     * 设置工厂方法名称
     */
    void setFactoryMethodName(String factoryMethodName);
    String getFactoryMethodName();  // 获取

    /**
     * 获取Bean的类名
     */
    String getBeanClassName();

    /**
     * 设置Bean的类名
     */
    void setBeanClassName(String beanClassName);

    /**
     * 设置是否自动装配候选
     */
    void setAutowireCandidate(boolean autowireCandidate);

    /**
     * 是否自动装配候选
     */
    boolean isAutowireCandidate();

    /**
     * 设置初始化方法名
     */
    void setInitMethodName(String initMethodName);

    /**
     * 获取初始化方法名
     */
    String getInitMethodName();

    /**
     * 设置销毁方法名
     */
    void setDestroyMethodName(String destroyMethodName);

    /**
     * 获取销毁方法名
     */
    String getDestroyMethodName();

}
