package com.spring.context.weaving;

import com.spring.beans.factory.HierarchicalBeanFactory;
import com.spring.beans.factory.ListableBeanFactory;
import com.spring.context.ApplicationEventPublisher;
import com.spring.context.MessageSource;
import com.spring.core.env.EnvironmentCapable;
import com.spring.core.io.support.ResourcePatternResolver;

/**
 * ClassName: ApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 15:26
 * @version: v1.0
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

}
