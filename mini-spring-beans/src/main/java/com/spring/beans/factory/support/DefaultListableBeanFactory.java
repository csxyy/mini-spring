package com.spring.beans.factory.support;

import com.spring.beans.factory.config.BeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: DefaultListableBeanFactory
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/24 - 23:48
 * @version: v1.0
 */
@Slf4j
public class DefaultListableBeanFactory implements BeanDefinitionRegistry{
    // 核心存储
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final List<String> beanDefinitionNames = new ArrayList<>();

    // 是否允许覆盖（简化：默认允许）
    private boolean allowBeanDefinitionOverriding = true;

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // 1. 基础校验
        if (beanName == null || beanName.isEmpty()) {
            throw new IllegalArgumentException("Bean 名称不得为 null 或空");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("BeanDefinition 不得为 null");
        }

        // 2. 检查是否已存在
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);

        // 3. 处理覆盖逻辑
        if (existingDefinition != null) {
            if (!allowBeanDefinitionOverriding) {
                throw new IllegalStateException("无法覆盖名称的 Bean 定义: " + beanName);
            }
            log.info("覆盖Bean定义: {}", beanName);
        } else {
            // 新注册的Bean，添加到名称列表
            this.beanDefinitionNames.add(beanName);
        }

        // 4. 注册到Map
        this.beanDefinitionMap.put(beanName, beanDefinition);

        // 5. 清除相关缓存（简化版）
        clearBeanDefinitionCache(beanName);

        log.info("注册Bean定义: {} -> {}", beanName, beanDefinition.getBeanClass().getSimpleName());
    }

    private void clearBeanDefinitionCache(String beanName) {
        // 简化版：后续实现合并BeanDefinition缓存时再完善
        // 目前先留空或简单日志
        log.info("清除Bean定义缓存: {}", beanName);
    }
}
