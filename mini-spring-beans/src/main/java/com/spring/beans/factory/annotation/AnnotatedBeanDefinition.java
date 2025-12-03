package com.spring.beans.factory.annotation;

import com.spring.beans.factory.config.BeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.MethodMetadata;

/**
 * ClassName: AnnotatedBeanDefinition
 * Description: 通过注解配置的Bean定义
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:01
 * @version: v1.0
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {
    /**
     * 获取注解元数据
     */
    AnnotationMetadata getMetadata();

    /**
     * 获取工厂方法元数据（如果有）
     */
    MethodMetadata getFactoryMethodMetadata();
}
