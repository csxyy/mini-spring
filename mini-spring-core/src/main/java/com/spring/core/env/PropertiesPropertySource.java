package com.spring.core.env;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ClassName: PropertiesPropertySource
 * Description: Properties文件属性源（自定义配置文件）
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:30
 * @version: v1.0
 */
@Slf4j
public class PropertiesPropertySource extends PropertySource{
    private final Properties properties;

    public PropertiesPropertySource(String name, Properties properties) {
        super(name);
        this.properties = properties;
    }

    public PropertiesPropertySource(String name, String location) {
        super(name);
        this.properties = loadProperties(location);
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    private Properties loadProperties(String location) {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(location)) {
            if (is != null) {
                props.load(is);
                log.info("加载配置文件: {} 成功，包含 {} 个属性", location, props.size());
            } else {
                log.warn("配置文件不存在: {}", location);
            }
        } catch (IOException e) {
            log.warn("加载配置文件失败: {}", location, e);
        }
        return props;
    }
}
