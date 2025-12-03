package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ConfigurationClass
 * Description: 配置类模型对象
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:11
 * @version: v1.0
 */
@Data
public class ConfigurationClass {
    private final AnnotatedBeanDefinition annotatedBeanDefinition;
    private final String beanName;

    public ConfigurationClass(AnnotatedBeanDefinition beanDefinition, String beanName) {
        this.annotatedBeanDefinition = beanDefinition;
        this.beanName = beanName;
    }

    public ConfigurationClass(Class<?> sourceClass, String beanName) {
        this.annotatedBeanDefinition = new AnnotatedGenericBeanDefinition(sourceClass);
        this.beanName = beanName;
    }

    // Getter/Setter 方法
    public AnnotatedBeanDefinition getAnnotatedBeanDefinition() {
        return annotatedBeanDefinition;
    }

    public Class<?> getSourceClass() {
        return annotatedBeanDefinition.getBeanClass();
    }

    public String getBeanName() {
        return beanName;
    }

    public AnnotationMetadata getMetadata() {
        return annotatedBeanDefinition.getMetadata();
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof ConfigurationClass &&
                this.getMetadata().getClassName().equals(((ConfigurationClass) other).getMetadata().getClassName())));
    }

    @Override
    public int hashCode() {
        return this.getMetadata().getClassName().hashCode();
    }
}
