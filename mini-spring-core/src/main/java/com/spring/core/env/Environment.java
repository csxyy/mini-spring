package com.spring.core.env;

/**
 * ClassName: Environment
 * Description:  基础环境接口
 *
 * @Author: csx
 * @Create: 2025/10/25 - 2:35
 * @version: v1.0
 */
public interface Environment {
    /** 获取属性值 */
    String getProperty(String key);

    /** 检查是否激活某个profile */
    boolean containsProperty(String key);
}
