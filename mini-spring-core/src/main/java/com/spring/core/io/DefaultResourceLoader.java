package com.spring.core.io;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.spring.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

/**
 * ClassName: DefaultResourceLoader
 * Description:
 *
 * 默认资源加载器 - 实现类路径和文件系统资源加载
 *
 * @Author: csx
 * @Create: 2025/11/4 - 15:18
 * @version: v1.0
 */
@AllArgsConstructor
@Setter
@Slf4j
public class DefaultResourceLoader implements ResourceLoader {
    private ClassLoader classLoader;

    public DefaultResourceLoader() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Resource getResource(String location) {
        log.debug("加载资源: {}", location);

        if (location.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // 支持classpath*:前缀（简化实现）
            return new ClassPathResource(location.substring(CLASSPATH_ALL_URL_PREFIX.length()), getClassLoader());
        } else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            // classpath:前缀
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        } else {
            // 默认作为类路径资源处理
            return new ClassPathResource(location, getClassLoader());
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = this.getClass().getClassLoader();
        }
        return this.classLoader;
    }
}
