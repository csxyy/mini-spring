package com.spring.core.io;

/**
 * ClassName: ResourceLoader
 * Description: 统一资源加载策略
 *
 * @Author: csx
 * @Create: 2025/11/4 - 15:17
 * @version: v1.0
 */
public interface ResourceLoader {
    /**
     * 获取资源
     * @param location
     * @return
     */
    Resource getResource(String location);

    /**
     * 获取类加载器
     * @return
     */
    ClassLoader getClassLoader();
}
