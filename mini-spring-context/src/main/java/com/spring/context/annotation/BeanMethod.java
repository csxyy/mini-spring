package com.spring.context.annotation;

import com.spring.core.type.MethodMetadata;
import com.spring.core.type.StandardMethodMetadata;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * ClassName: BeanMethod
 * Description: Bean方法模型
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:26
 * @version: v1.0
 */
@Getter
public class BeanMethod {
    private final Method method;
    private final MethodMetadata methodMetadata;

    public BeanMethod(Method method) {
        this.method = method;
        this.methodMetadata = new StandardMethodMetadata(method);
    }

    public Method getMethod() {
        return method;
    }

    public MethodMetadata getMethodMetadata() {
        return methodMetadata;
    }

    public String getMethodName() {
        return method.getName();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }
}
