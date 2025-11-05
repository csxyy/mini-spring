package com.spring.beans.factory.support;

import com.spring.beans.factory.config.ConfigurableBeanFactory;
import com.spring.context.support.BeanPostProcessor;
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
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

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


        return null;
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

}
