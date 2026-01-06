package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName: ConfigurationClass
 * Description: 配置类模型对象
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:11
 * @version: v1.0
 */
@Slf4j
@Data
public class ConfigurationClass {
    private final AnnotatedBeanDefinition annotatedBeanDefinition;
    private final String beanName;
    private final Set<BeanMethod> beanMethods = new LinkedHashSet<>();

    public ConfigurationClass(AnnotatedBeanDefinition beanDefinition, String beanName) {
        this.annotatedBeanDefinition = beanDefinition;
        this.beanName = beanName;
    }

    public ConfigurationClass(Class<?> sourceClass, String beanName) {
        this.annotatedBeanDefinition = new AnnotatedGenericBeanDefinition(sourceClass);
        this.beanName = beanName;
    }

    // Getter/Setter 方法

    /**
     * 添加Bean方法
     */
    public void addBeanMethod(BeanMethod beanMethod) {
        this.beanMethods.add(beanMethod);
        log.debug("为配置类 {} 添加Bean方法: {}",
                getMetadata().getClassName(), beanMethod.getMethodName());
    }


    /**
     * 获取所有Bean方法
     */
    public List<BeanMethod> getBeanMethods() {
        return new ArrayList<>(beanMethods);
    }

    /**
     * 清除Bean方法（用于重新处理）
     */
    public void clearBeanMethods() {
        this.beanMethods.clear();
    }

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
