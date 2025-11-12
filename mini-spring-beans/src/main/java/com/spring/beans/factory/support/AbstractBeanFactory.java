package com.spring.beans.factory.support;

import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.BeanFactoryUtils;
import com.spring.beans.factory.FactoryBean;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.ConfigurableBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ClassName: AbstractBeanFactory
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:47
 * @version: v1.0
 */
@Slf4j
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport
        implements ConfigurableBeanFactory, BeanDefinitionRegistry {

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    /** FactoryBean创建的对象缓存 */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
        log.debug("添加BeanPostProcessor: {}", beanPostProcessor.getClass().getSimpleName());
    }

    @Override
    public Object getBean(String name) {
        return doGetBean(name, null, null, false);
    }


    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return doGetBean(name, requiredType, null, false);
    }

    protected <T> T doGetBean(
            String name, Class<T> requiredType, Object[] args, boolean typeCheckOnly) {
        log.debug("开始获取Bean: {}", name);

        // 1. 名称转换
        String beanName = transformedBeanName(name);
        log.debug("转换后的Bean名称: {}", beanName);

        // 2. 检查单例缓存
        Object bean = getSingleton(beanName);
        if (bean != null) {
            log.debug("从单例缓存中获取Bean: {}", beanName);
            // 处理FactoryBean：根据原始name决定返回FactoryBean本身还是其创建的对象
            return (T) getObjectForBeanInstance(bean, name, beanName, null);
        }

        // 3. 获取合并的BeanDefinition
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        log.debug("获取到BeanDefinition: {}", beanName);

        // 4. 处理依赖关系
//        String[] dependsOn = mbd.getDependsOn();
//        if (dependsOn != null) {
//            for (String dep : dependsOn) {
//                log.debug("处理依赖Bean: {} 依赖于 {}", beanName, dep);
//                getBean(dep); // 递归获取依赖的Bean
//            }
//        }

        // 5. 根据作用域创建Bean
        if (mbd.isSingleton()) {
            log.debug("创建单例Bean: {}", beanName);
            bean = getSingleton(beanName, () -> createBean(beanName, mbd, args));
        } else {
            log.debug("创建原型Bean: {}", beanName);
            bean = createBean(beanName, mbd, args);
        }

        // 6. 处理FactoryBean
//        bean = getObjectForBeanInstance(bean, name, beanName);

        log.debug("成功获取Bean: {} -> {}", name,
                bean != null ? bean.getClass().getSimpleName() : "null");
        return (T) bean;
    }

    protected String transformedBeanName(String name) {
        return BeanFactoryUtils.transformedBeanName(name);
    }

    /**
     * 处理Bean实例 - 区分FactoryBean和普通Bean
     * 作用：
     * 1. 如果name以&开头，返回FactoryBean本身
     * 2. 如果是普通Bean，直接返回
     * 3. 如果是FactoryBean且name不以&开头，返回FactoryBean创建的对象
     */
    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {
        log.debug("处理Bean实例: name={}, beanName={}, beanType={}",
                name, beanName, beanInstance.getClass().getSimpleName());

        // 1. 检查是否是FactoryBean引用（name以&开头）
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (!(beanInstance instanceof FactoryBean)) {
                throw new RuntimeException("Bean '" + beanName + "' 的类型是 '" +
                        beanInstance.getClass().getName() + "', 但它不是FactoryBean");
            }
            log.debug("返回FactoryBean本身: {}", beanName);
            return beanInstance;
        }

        // 2. 如果不是FactoryBean，直接返回实例
        if (!(beanInstance instanceof FactoryBean)) {
            log.debug("返回普通Bean实例: {}", beanName);
            return beanInstance;
        }

        // 3. 处理FactoryBean创建的对象
        Object object = null;
        // 尝试从缓存中获取
        object = getCachedObjectForFactoryBean(beanName);
        if (object != null) {
            log.debug("从缓存获取FactoryBean创建的对象: {}", beanName);
            return object;
        }

        // 缓存中没有，调用FactoryBean.getObject()方法创建对象
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        object = getObjectFromFactoryBean(factory, beanName);

        log.debug("FactoryBean创建对象完成: {} -> {}", beanName,
                object != null ? object.getClass().getSimpleName() : "null");
        return object;
    }

    /**
     * 从缓存中获取FactoryBean创建的对象
     */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /**
     * 从FactoryBean获取对象
     */
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        // 如果是单例，需要缓存
        if (factory.isSingleton()) {
            synchronized (this.factoryBeanObjectCache) {
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    object = factory.getObject();
                    // 直接缓存对象，null也缓存（避免重复调用getObject()）
                    this.factoryBeanObjectCache.put(beanName, object);
                    log.debug("缓存FactoryBean创建的对象: {}", beanName);
                }
                return object;
            }
        } else {
            // 原型模式，每次都创建新对象
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }

    /**
     * 实际调用FactoryBean.getObject()方法
     */
    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        log.debug("调用FactoryBean.getObject(): {}", beanName);
        Object object = factory.getObject();
        if (object == null) {
            log.debug("FactoryBean返回null对象: {}", beanName);
        }
        return object;
    }

    /**
     * 合并BeanDefinition
     *
     * 在Spring完整版需要合并的场景一般都是有父子Bean定义
     * 一般情况下，大部分常见都不会真实合并
     *
     * 特殊情况：继承配置类（很少用）这种情况会真正走合并逻辑，但一般我们都不会这么写
     * @Configuration
     * public class BaseConfig {}
     *
     * @Configuration // 这里不会继承Bean定义，需要显式@Import
     * public class AppConfig extends BaseConfig {}
     *
     * 注意：我们这里只实现基本的获取RootBeanDefinition的逻辑
     */
    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) {
        log.debug("获取BeanDefinition: {}", beanName);

        // 直接获取原始BeanDefinition
        BeanDefinition bd = getBeanDefinition(beanName);

        // 如果是RootBeanDefinition，直接返回
        if (bd instanceof RootBeanDefinition) {
            RootBeanDefinition rbd = (RootBeanDefinition) bd;
            rbd.validate(); // 简单验证
            return rbd;
        }

        // 如果不是RootBeanDefinition，包装一下（几乎不会走到这里）
        return new RootBeanDefinition(bd.getBeanClass());
    }

    /**
     * 创建Bean实例
     */
    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, Object[] args);

    /**
     * 获取Bean的Class实例
     */
    protected Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?>... typesToMatch) {
        // 真实Spring中BeanDefinition里的beanClass类型是Object的，所以会有很多判断逻辑
        // 而我们的beanClass类型就是Class直接获取即可
        return mbd.getBeanClass();
    }

    @Override
    public boolean containsBean(String name) {
        // TODO
        return true;
    }

    @Override
    public boolean isFactoryBean(String name) {
        // TODO
        return false;
    }

    @Override
    public boolean containsLocalBean(String name) {
        // 简化实现：检查单例缓存和Bean定义
        return true;
    }

    /**
     * 检查Bean是否是单例
     */
    @Override
    public boolean isSingleton(String beanName) {
        BeanDefinition bd = getBeanDefinition(beanName);
        if (bd != null) {
            return bd.isSingleton();
        }

        // 如果是手动注册的单例，也返回true
        return containsSingleton(beanName);
    }

    /**
     * 判断类型是否匹配
     */
    protected boolean isTypeMatch(String beanName, Class<?> targetType) {
        try {
            Class<?> beanType = getType(beanName);
            if (beanType == null) {
                return false;
            }

            // 检查类型是否匹配（包括父子类、接口实现）
            return targetType.isAssignableFrom(beanType);

        } catch (Exception e) {
            log.debug("检查Bean类型匹配失败: {}, {}", beanName, targetType.getSimpleName(), e);
            return false;
        }
    }

    /**
     * 获取Bean的类型
     */
    @Override
    public Class<?> getType(String beanName) {
        BeanDefinition bd = getBeanDefinition(beanName);

        // 1. 如果BeanDefinition中直接指定了类，直接返回
        if (bd.getBeanClass() != null) {
            return bd.getBeanClass();
        }

        // 2. 如果是工厂Bean，需要特殊处理（简化版先返回null）
//        if (bd.getFactoryBeanName() != null || bd.getFactoryMethodName() != null) {
//            // 完整Spring会在这里解析工厂方法的返回类型
//            log.debug("工厂Bean的类型推断暂未实现: {}", beanName);
//            return null;
//        }

        // 3. 如果都没有，返回null
        return null;
    }
}
