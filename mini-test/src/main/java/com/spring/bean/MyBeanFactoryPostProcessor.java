package com.spring.bean;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import com.spring.stereotype.Component;

/**
 * ClassName: MyBeanFactoryPostProcessor
 * Description:
 *
 * @Author: csx
 * @Create: 2025/12/3 - 15:28
 * @version: v1.0
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        System.out.println("MyBeanFactoryPostProcessor#postProcessBeanFactory...");
    }
}
