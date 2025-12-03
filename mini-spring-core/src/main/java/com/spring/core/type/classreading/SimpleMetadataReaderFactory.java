package com.spring.core.type.classreading;

import com.spring.core.io.ClassPathResource;
import com.spring.core.io.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * ClassName: SimpleMetadataReaderFactory
 * Description:
 *
 * 简单的元数据读取器工厂
 *
 * @Author: csx
 * @Create: 2025/12/3 - 0:03
 * @version: v1.0
 */
@Slf4j
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        log.debug("为资源创建MetadataReader: {}", resource.getDescription());
        return new SimpleMetadataReader(resource);
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        log.debug("为类名创建MetadataReader: {}", className);
        // 简化实现：通过类加载器加载资源
        Resource resource = new ClassPathResource(className.replace('.', '/') + ".class");
        return getMetadataReader(resource);
    }
}
