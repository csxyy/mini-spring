package com.spring.core.env;

import lombok.AllArgsConstructor;

/**
 * ClassName: PropertySource
 * Description: 属性源抽象
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:18
 * @version: v1.0
 */
@AllArgsConstructor
public abstract class PropertySource {
    protected final String name;

    public abstract String getProperty(String key);
    public abstract boolean containsProperty(String key);
}
