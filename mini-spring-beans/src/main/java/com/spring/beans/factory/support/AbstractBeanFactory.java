package com.spring.beans.factory.support;

import com.spring.beans.factory.config.ConfigurableBeanFactory;
import com.spring.context.support.BeanPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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
//        return doGetBean(name, null, null, false);
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        // TODO
        return true;
    }
}
