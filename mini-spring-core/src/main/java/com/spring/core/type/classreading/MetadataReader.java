package com.spring.core.type.classreading;

import com.spring.core.io.Resource;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.ClassMetadata;

/**
 * ClassName: MetadataReader
 * Description:
 *
 * 元数据读取器接口 - 对应Spring的MetadataReader
 * 用于读取类的元数据信息，而不实际加载类
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:54
 * @version: v1.0
 */
public interface MetadataReader {

    /**
     * 获取资源引用
     */
    Resource getResource();

    /**
     * 获取类元数据
     */
    ClassMetadata getClassMetadata();

    /**
     * 获取注解元数据
     */
    AnnotationMetadata getAnnotationMetadata();
}
