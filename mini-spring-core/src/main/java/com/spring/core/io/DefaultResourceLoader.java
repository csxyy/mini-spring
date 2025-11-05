package com.spring.core.io;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ClassName: DefaultResourceLoader
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/4 - 15:18
 * @version: v1.0
 */
@AllArgsConstructor
@Setter
public class DefaultResourceLoader implements ResourceLoader {
    private ClassLoader classLoader;

    public DefaultResourceLoader() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Resource getResource(String location) {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = this.getClass().getClassLoader();
        }
        return this.classLoader;
    }
}
