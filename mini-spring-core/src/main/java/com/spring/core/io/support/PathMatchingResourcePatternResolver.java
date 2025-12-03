package com.spring.core.io.support;

import com.spring.core.io.DefaultResourceLoader;
import com.spring.core.io.FileSystemResource;
import com.spring.core.io.Resource;
import com.spring.core.io.ResourceLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: PathMatchingResourcePatternResolver
 * Description:
 *
 * 路径匹配资源模式解析器 - 简化版本
 * Spring原版支持Ant风格路径匹配，这里先实现基础功能
 *
 * @Author: csx
 * @Create: 2025/11/4 - 22:10
 * @version: v1.0
 */
@Slf4j
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private final ResourceLoader resourceLoader;

    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

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
        log.debug("获取匹配的资源: {}", locationPattern);

        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // classpath*:模式 - 需要扫描所有匹配的资源
            // 简化实现：先获取当前类路径下的资源
            return findPathMatchingResources(locationPattern);
        } else {
            // 单个资源
            return new Resource[]{getResource(locationPattern)};
        }
    }

    /**
     * 查找路径匹配的资源 - 对应Spring的findPathMatchingResources方法
     * 简化实现：支持基础的classpath*:扫描
     */
    private Resource[] findPathMatchingResources(String locationPattern) {
        log.debug("查找路径匹配资源: {}", locationPattern);

        // 移除classpath*:前缀
        String rootDirPath = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
        int starIndex = rootDirPath.indexOf('*');
        if (starIndex != -1) {
            // 如果包含*，截取到*之前的部分作为根目录
            rootDirPath = rootDirPath.substring(0, starIndex);
        }

        // 获取根目录资源
        Resource rootDirResource = getResource(CLASSPATH_URL_PREFIX + rootDirPath);

        if (rootDirResource.exists() && rootDirResource.isFile()) {
            try {
                // 文件系统资源：遍历目录
                File rootDir = rootDirResource.getFile();
                if (rootDir.isDirectory()) {
                    return findFileSystemResources(rootDir, locationPattern);
                }
            } catch (IOException ex) {
                log.warn("无法访问文件系统资源: {}", rootDirResource.getDescription(), ex);
            }
        }

        // 简化：返回空数组，后续可以增强支持jar包等
        log.debug("未找到匹配的资源，返回空数组");
        return new Resource[0];
    }

    /**
     * 在文件系统中查找匹配的资源
     */
    private Resource[] findFileSystemResources(File rootDir, String pattern) throws IOException {
        List<Resource> resources = new ArrayList<>();

        // 提取通配符后面的部分
        String subPattern = pattern.substring(pattern.indexOf('*') + 1);

        // 递归遍历目录
        collectResources(rootDir, subPattern, resources);

        log.debug("在目录 {} 中找到 {} 个匹配的资源", rootDir, resources.size());
        return resources.toArray(new Resource[0]);
    }

    /**
     * 递归收集资源
     */
    private void collectResources(File dir, String pattern, List<Resource> resources) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子目录
                collectResources(file, pattern, resources);
            } else if (file.getName().endsWith(".class")) {
                // 只处理.class文件
                String relativePath = getRelativePath(file);
                if (matchesPattern(relativePath, pattern)) {
                    resources.add(new FileSystemResource(file));
                }
            }
        }
    }

    /**
     * 获取相对路径
     */
    private String getRelativePath(File file) {
        // 简化实现：返回文件名
        return file.getName();
    }

    /**
     * 检查是否匹配模式
     */
    private boolean matchesPattern(String path, String pattern) {
        // 简化：只要以.class结尾就匹配
        return path.endsWith(".class");
    }
}
