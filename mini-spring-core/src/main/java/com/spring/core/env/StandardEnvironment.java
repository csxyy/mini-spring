package com.spring.core.env;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: StandardEnvironment
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:14
 * @version: v1.0
 */
@Slf4j
public class StandardEnvironment implements ConfigurableEnvironment{
    private final List<PropertySource> propertySources = new ArrayList<>();
    private final Set<String> requiredProperties = new HashSet<>();

    public StandardEnvironment() {
        // 初始化三个核心属性源（按Spring的优先级顺序）
        // 1. 系统属性（最高优先级）
        addPropertySource(new SystemPropertySource());
        // 2. 系统环境变量
        addPropertySource(new SystemEnvPropertySource());
        // 3. 默认的application.properties（最低优先级）
        addPropertySource(new PropertiesPropertySource("applicationConfig", "application.properties"));
    }

    @Override
    public String getProperty(String key) {
        // 按优先级从属性源中查找（顺序查找，先找到的返回）
        for (PropertySource propertySource : propertySources) {
            if (propertySource.containsProperty(key)) {
                String value = propertySource.getProperty(key);
                log.debug("从 [{}] 获取属性: {} = {}", propertySource.name, key, value);
                return value;
            }
        }
        log.debug("属性未找到: {}", key);
        return null;
    }

    @Override
    public boolean containsProperty(String key) {
        return getProperty(key) != null;
    }

    @Override
    public void setRequiredProperties(String... requiredProperties) {
        Collections.addAll(this.requiredProperties, requiredProperties);
        log.info("设置必需属性: {}", Arrays.toString(requiredProperties));
    }

    @Override
    public void validateRequiredProperties() {
        List<String> missing = new ArrayList<>();
        for (String requiredProperty : requiredProperties) {
            if (!containsProperty(requiredProperty)) {
                missing.add(requiredProperty);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("缺少必需的配置属性: " + missing);
        }
        log.info("必需属性验证通过，共验证 {} 个属性", requiredProperties.size());
    }

    @Override
    public void addPropertySource(PropertySource propertySource) {
        this.propertySources.add(propertySource);
        log.info("添加属性源: {}", propertySource.name);
    }

    // 新增：获取所有属性源信息（用于调试）
    public List<String> getPropertySourceNames() {
        return propertySources.stream()
                .map(ps -> ps.name)
                .collect(Collectors.toList());
    }
}
