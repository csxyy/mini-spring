package com.spring.core.env;

/**
 * ClassName: SystemEnvPropertySource
 * Description: 系统环境变量源（OS环境变量）
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:21
 * @version: v1.0
 */
public class SystemEnvPropertySource extends PropertySource {
    public SystemEnvPropertySource() {
        super("systemEnvironment");
    }

    @Override
    public String getProperty(String key) {
        return System.getenv(key);
    }

    @Override
    public boolean containsProperty(String key) {
        return System.getenv(key) != null;
    }
}
