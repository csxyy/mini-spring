package com.spring.context.support;

import com.spring.context.ApplicationContextAware;
import com.spring.context.ApplicationEventPublisherAware;
import com.spring.context.EnvironmentAware;
import com.spring.context.weaving.ApplicationContext;
import com.spring.context.weaving.ConfigurableApplicationContext;
import lombok.AllArgsConstructor;

/**
 * ClassName: ApplicationContextAwareProcessor
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:32
 * @version: v1.0
 */
@AllArgsConstructor
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // 简化实现：如果Bean实现了Aware接口，注入相应的依赖
        if (bean instanceof EnvironmentAware) {
            ((EnvironmentAware) bean).setEnvironment(applicationContext.getEnvironment());
        }
        if (bean instanceof ApplicationEventPublisherAware) {
            ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(applicationContext);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
