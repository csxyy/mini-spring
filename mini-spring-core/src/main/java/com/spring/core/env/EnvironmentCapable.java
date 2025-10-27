package com.spring.core.env;

/**
 * ClassName: EnvironmentCapable
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/27 - 11:54
 * @version: v1.0
 */
public interface EnvironmentCapable {

    /** 返回与此组件关联的 Environment */
    Environment getEnvironment();

}
