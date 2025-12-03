package com.spring.core.type.classreading;

import com.spring.core.io.Resource;

import java.io.IOException;

/**
 * ClassName: MetadataReaderFactory
 * Description:
 *
 * 元数据读取器工厂 - 对应Spring的MetadataReaderFactory
 *
 * @Author: csx
 * @Create: 2025/12/3 - 0:02
 * @version: v1.0
 */
public interface MetadataReaderFactory {

    /**
     * 获取元数据读取器
     */
    MetadataReader getMetadataReader(Resource resource) throws IOException;

    /**
     * 获取元数据读取器（通过类名）
     */
    MetadataReader getMetadataReader(String className) throws IOException;
}
