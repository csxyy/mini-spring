package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName: ConfigurationClassUtils
 * Description: 配置类工具类
 *
 * 负责检查Bean定义是否为配置类候选者，判断full/lite模式等
 *
 * @Author: csx
 * @Create: 2025/11/13 - 17:37
 * @version: v1.0
 */
@Slf4j
public class ConfigurationClassUtils {
    /**
     * 检查Bean定义是否为配置类候选者
     * 这个方法在Spring原版中会：
     * 1. 检查类上的注解（@Configuration, @Component, @ComponentScan, @Import, @ImportResource）
     * 2. 判断配置类是full模式还是lite模式
     * 3. 设置BeanDefinition的属性标识
     */
    public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef) {
        log.debug("开始检查BeanDefinition是否为配置类候选者");

        // 关键修改：使用AnnotatedBeanDefinition接口检查，而不是具体实现类
        if (!(beanDef instanceof AnnotatedBeanDefinition)) {
            log.debug("BeanDefinition不是AnnotatedBeanDefinition，跳过配置类检查");
            return false;
        }

        AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDef;
        AnnotationMetadata metadata = abd.getMetadata();

        // 使用AnnotatedBeanDefinition提供的注解元数据能力
        boolean isConfigClass = isConfigurationAnnotationPresent(metadata);

        if (isConfigClass) {
            log.debug("识别到配置类: {}", metadata.getClassName());

            // 设置配置类属性
            setConfigurationClassAttribute(beanDef, metadata);
        }

        return isConfigClass;
    }

    /**
     * 检查是否存在配置类相关的注解
     * Spring原版逻辑：@Configuration, @Component, @ComponentScan, @Import, @ImportResource（xml）
     */
    private static boolean isConfigurationAnnotationPresent(AnnotationMetadata metadata) {
        return metadata.hasAnnotation(Configuration.class.getName()) ||
                // @Component检查主要是为了识别@Configuration(它被@Component标注)
                metadata.isAnnotated(Component.class.getName()) ||
                metadata.hasAnnotation(ComponentScan.class.getName()) ||
                metadata.hasAnnotation(Import.class.getName());
    }

    /**
     * 设置配置类属性 - 对应Spring设置CONFIGURATION_CLASS_ATTRIBUTE的逻辑
     * Spring原版会区分full模式和lite模式，我们这里简化实现
     */
    private static void setConfigurationClassAttribute(BeanDefinition beanDef, AnnotationMetadata metadata) {
        // Spring原版逻辑：
        // 如果是@Configuration注解，设置为"full"
        // 如果是其他配置相关注解，设置为"lite"
        if (metadata.hasAnnotation(Configuration.class.getName())) {
            log.debug("设置配置类为full模式: {}", metadata.getClassName());
            // beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
        } else {
            log.debug("设置配置类为lite模式: {}", metadata.getClassName());
            // beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        }
    }
}
