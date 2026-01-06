package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.util.ObjectUtils;

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

    private String beanClassName;

    private String scope = SCOPE_SINGLETON;

    private Boolean lazyInit = false;   // 懒加载

    private boolean abstractFlag = false;   // 非抽象

    private boolean primary = false;

    private String factoryBeanName; // 工厂Bean名称
    private String factoryMethodName;   // 工厂方法名称

    private Object source;  // 添加source属性

    /**
     * 自动装配模式常量 - 对应Spring的AbstractBeanDefinition
     */
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    public static final int AUTOWIRE_AUTODETECT = 4;

    private int autowireMode = AUTOWIRE_NO; // 自动装配模式

    private boolean autowireCandidate = true; // 是否自动装配候选（默认开启）

    private String[] initMethodNames; // 初始化方法

    private String[] destroyMethodNames; // 销毁方法


    /**
     * 设置BeanDefinition默认值
     */
    public void applyDefaults(BeanDefinitionDefaults defaults) {
        Boolean lazyInit = defaults.getLazyInit();
        if (lazyInit != null) {
            setLazyInit(lazyInit);
        }

        // 下面这些配置在目前的注解驱动中都用不到可以不用管
        //setAutowireMode(defaults.getAutowireMode());
        //setDependencyCheck(defaults.getDependencyCheck());
        //setInitMethodName(defaults.getInitMethodName());
        //setDestroyMethodName(defaults.getDestroyMethodName());
    }

    /**
     * 获取Bean类 - 延迟加载版本
     * 对应Spring的AbstractBeanDefinition.getBeanClass()逻辑
     */
    @Override
    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null && this.beanClassName != null) {
            // 延迟加载类
            beanClassObject = resolveBeanClass();
            this.beanClass = (Class<?>) beanClassObject;
        }
        return (Class<?>) beanClassObject;
    }

    /**
     * 解析Bean类 - 对应Spring的resolveBeanClass()方法
     * 当需要实例化时，才真正加载类
     */
    protected Class<?> resolveBeanClass() throws IllegalStateException {
        String className = getBeanClassName();
        if (className == null) {
            throw new IllegalStateException("Bean定义没有指定类名");
        }

        try {
            // 使用BeanDefinition的类加载器加载类
            ClassLoader classLoader = getBeanClassLoader();
            if (classLoader != null) {
                return Class.forName(className, false, classLoader);
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("无法加载Bean类 [" + className + "]", ex);
        }

        // 使用默认类加载器
        try {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("无法加载Bean类 [" + className + "]", ex);
        }
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 获取Bean的类名
     */
    @Override
    public String getBeanClassName() {
        if (this.beanClass != null) {
            return this.beanClass.getName();
        }
        return this.beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        this.beanClass = null; // 清空缓存的Class对象
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

    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    /**
     * 设置工厂Bean名称
     */
    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    /**
     * 获取工厂Bean名称
     */
    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    /**
     * 设置工厂方法名称
     */
    @Override
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    /**
     * 获取工厂方法名称
     */
    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }


    /**
     * 检查是否是工厂Bean
     */
    public boolean isFactoryBean() {
        return (this.factoryBeanName != null || this.factoryMethodName != null);
    }

    /**
     * 检查是否有实例提供者（工厂方法）
     */
    public boolean hasInstanceProvider() {
        return (this.factoryBeanName != null && this.factoryMethodName != null);
    }

    /**
     * 获取Bean类加载器 - 简化实现
     */
    protected ClassLoader getBeanClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 判断是否有Bean类（已经加载）
     */
    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }


    /**
     * 获取源对象（用于调试和元数据）
     */
    public Object getSource() {
        return source;
    }

    /**
     * 设置源对象
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * 复制BeanDefinitio
     * @return
     */
    public abstract AbstractBeanDefinition cloneBeanDefinition();

    /**
     * 设置自动装配模式
     */
    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    /**
     * 获取自动装配模式
     */
    public int getAutowireMode() {
        return this.autowireMode;
    }

    /**
     * 设置是否自动装配候选
     */
    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    /**
     * 是否自动装配候选
     */
    @Override
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }

    /**
     * 设置初始化方法名
     */
    @Override
    public void setInitMethodName(String initMethodName) {
        this.initMethodNames = (initMethodName != null ? new String[] {initMethodName} : null);
    }

    /**
     * 获取初始化方法名
     */
    @Override
    public String getInitMethodName() {
        return (!ObjectUtils.isEmpty(this.initMethodNames) ? this.initMethodNames[0] : null);
    }

    /**
     * 设置销毁方法名
     */
    @Override
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodNames = (destroyMethodName != null ? new String[] {destroyMethodName} : null);
    }

    /**
     * 获取销毁方法名
     */
    @Override
    public String getDestroyMethodName() {
        return (!ObjectUtils.isEmpty(this.destroyMethodNames) ? this.destroyMethodNames[0] : null);
    }
}
