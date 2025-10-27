package com.spring.core.env;

/**
 * ClassName: SystemPropertySource
 * Description: 系统属性源（JVM属性）
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:20
 * @version: v1.0
 */
public class SystemPropertySource extends PropertySource {
    public SystemPropertySource() {
        super("systemProperties");
    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public boolean containsProperty(String key) {
        return System.getProperty(key) != null;
    }
}
