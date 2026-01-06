package com.spring.context.annotation;

import com.spring.core.type.MethodMetadata;
import com.spring.core.type.StandardMethodMetadata;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * ClassName: BeanMethod
 * Description: Bean方法模型
 *
 * Bean方法封装类 - 对应Spring的BeanMethod
 * 用于在配置类解析阶段保存@Bean方法信息
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:26
 * @version: v1.0
 */
@Slf4j
@Getter
public class BeanMethod {
    private final MethodMetadata metadata;
    private final ConfigurationClass configurationClass;
    private final Method method;

    /**
     * 构造方法 - 基于MethodMetadata和ConfigurationClass
     * 对应Spring的BeanMethod构造方法
     */
    public BeanMethod(MethodMetadata methodMetadata, ConfigurationClass configurationClass) {
        this.metadata = methodMetadata;
        this.configurationClass = configurationClass;

        // 如果MethodMetadata是StandardMethodMetadata，提取Method对象
        if (methodMetadata instanceof StandardMethodMetadata smm) {
            this.method = smm.getMethod();
        } else {
            this.method = null;
        }

        log.debug("创建BeanMethod: {} -> {}",
                getMethodName(), getReturnTypeName());
    }

    /**
     * 构造方法 - 向后兼容，基于Method对象
     */
    @Deprecated
    public BeanMethod(Method method) {
        this.method = method;
        this.metadata = new StandardMethodMetadata(method);
        this.configurationClass = null;
    }

    /**
     * 获取方法名
     */
    public String getMethodName() {
        return metadata.getMethodName();
    }

    /**
     * 获取返回类型名
     */
    public String getReturnTypeName() {
        return metadata.getReturnTypeName();
    }

    /**
     * 获取声明类名
     */
    public String getDeclaringClassName() {
        return metadata.getDeclaringClassName();
    }

    /**
     * 获取@Bean注解属性
     */
    public Map<String, Object> getBeanAttributes() {
        return metadata.getAnnotationAttributes(Bean.class.getName());
    }

    /**
     * 获取指定的Bean名称（如果有的话）
     */
    public String[] getBeanNames() {
        Map<String, Object> attributes = getBeanAttributes();
        if (attributes != null && attributes.containsKey("beanNames")) {
            return (String[]) attributes.get("beanNames");
        }
        return new String[0];
    }

    /**
     * 是否自动装配候选
     */
    public boolean isAutowireCandidate() {
        Map<String, Object> attributes = getBeanAttributes();
        if (attributes != null && attributes.containsKey("autowireCandidate")) {
            return (Boolean) attributes.get("autowireCandidate");
        }
        return true; // 默认值
    }

    /**
     * 获取初始化方法名
     */
    public String getInitMethod() {
        Map<String, Object> attributes = getBeanAttributes();
        if (attributes != null && attributes.containsKey("initMethod")) {
            return (String) attributes.get("initMethod");
        }
        return "";
    }

    /**
     * 获取销毁方法名
     */
    public String getDestroyMethod() {
        Map<String, Object> attributes = getBeanAttributes();
        if (attributes != null && attributes.containsKey("destroyMethod")) {
            return (String) attributes.get("destroyMethod");
        }
        return "";
    }

    /**
     * 判断方法是否静态
     */
    public boolean isStatic() {
        return metadata.isStatic();
    }

    @Override
    public String toString() {
        return "BeanMethod{" +
                "methodName='" + getMethodName() + '\'' +
                ", returnType=" + getReturnTypeName() +
                ", declaringClass=" + getDeclaringClassName() +
                '}';
    }
}
