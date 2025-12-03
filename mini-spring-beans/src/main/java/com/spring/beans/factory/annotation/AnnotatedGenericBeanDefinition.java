package com.spring.beans.factory.annotation;

import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.MethodMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import com.spring.core.type.classreading.MetadataReader;

/**
 * ClassName: AnnotatedGenericBeanDefinition
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/12 - 22:06
 * @version: v1.0
 */
public class AnnotatedGenericBeanDefinition extends RootBeanDefinition implements AnnotatedBeanDefinition {
    private final AnnotationMetadata annotationMetadata;
    private MethodMetadata factoryMethodMetadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        super(beanClass);
        this.annotationMetadata = new StandardAnnotationMetadata(beanClass);
    }

    /**
     * 基于元数据创建BeanDefinition的构造方法
     * 对应Spring的ScannedGenericBeanDefinition构造方法
     */
    public AnnotatedGenericBeanDefinition(MetadataReader metadataReader) {
        super();
        this.annotationMetadata = metadataReader.getAnnotationMetadata();
        // 设置beanClass为null，因为我们只有元数据，还没有加载类
        setBeanClassName(metadataReader.getClassMetadata().getClassName());
    }

    /**
     * 获取注解元数据 - 如果需要类信息，延迟加载类
     */
    @Override
    public AnnotationMetadata getMetadata() {
        return this.annotationMetadata;
    }

    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }

    public void setFactoryMethodMetadata(MethodMetadata factoryMethodMetadata) {
        this.factoryMethodMetadata = factoryMethodMetadata;
    }
}
