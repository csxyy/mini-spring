package com.spring.core.io.support;

import com.spring.core.io.Resource;
import com.spring.core.io.ResourceLoader;

/**
 * ClassName: PathMatchingResourcePatternResolver
 * Description:
 *
 * @Author: csx
 * @Create: 2025/11/4 - 22:10
 * @version: v1.0
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private final ResourceLoader resourceLoader;

    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    @Override
    public Resource[] getResources(String locationPattern) {
        return new Resource[] {getResourceLoader().getResource(locationPattern)};
    }
}
