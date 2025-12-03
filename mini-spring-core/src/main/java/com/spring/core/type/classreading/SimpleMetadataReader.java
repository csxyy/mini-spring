package com.spring.core.type.classreading;

import com.spring.core.io.FileSystemResource;
import com.spring.core.io.Resource;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.ClassMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import com.spring.core.type.StandardClassMetadata;
import com.spring.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * ClassName: SimpleMetadataReader
 * Description:
 *
 * 简单的元数据读取器实现 - 使用反射而不是ASM
 *  *
 *  * 设计选择：使用反射而不是ASM的原因：
 *  * 1. 简化实现，避免引入ASM的复杂性
 *  * 2. 在mini项目中，性能不是首要考虑
 *  * 3. 反射API更直观，便于理解Spring的设计思想
 *  * 4. 后续如果需要改为ASM，可以保持接口不变
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:58
 * @version: v1.0
 */
@Slf4j
public class SimpleMetadataReader implements MetadataReader {

    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public SimpleMetadataReader(Resource resource) throws IOException {
        this.resource = resource;

        // 从资源中获取类名
        String className = getClassNameFromResource(resource);

        // 使用反射加载类（这里会实际加载类，但简化了实现）
        // Spring原版使用ASM避免加载类，我们这里使用反射简化
        try {
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            Class<?> clazz = Class.forName(className, false, classLoader);

            this.classMetadata = new StandardClassMetadata(clazz);
            this.annotationMetadata = new StandardAnnotationMetadata(clazz);

            log.debug("创建MetadataReader: {}", className);
        } catch (ClassNotFoundException e) {
            throw new IOException("无法加载类: " + className, e);
        }
    }

    /**
     * 从资源中提取类名
     * 例如：从文件路径 com/example/User.class 转换为 com.example.User
     */
    private String getClassNameFromResource(Resource resource) throws IOException {
        String filename = resource.getFilename();
        if (filename == null) {
            throw new IOException("资源没有文件名: " + resource.getDescription());
        }

        // 获取资源路径（相对路径）
        String path = resource.getURL().getPath();

        // 简化实现：假设是文件系统资源
        // 在实际的Spring中，这个逻辑更复杂，支持JAR包等
        if (resource instanceof FileSystemResource) {
            File file = ((FileSystemResource) resource).getFile();
            return extractClassNameFromFile(file);
        }

        // 默认处理：从文件名中提取
        String className = filename.substring(0, filename.length() - 6); // 去掉".class"
        log.debug("从文件名提取类名: {} -> {}", filename, className);
        return className;
    }

    /**
     * 从文件路径提取类名
     */
    private String extractClassNameFromFile(File classFile) {
        // 简化实现：假设基础包路径已知
        // 在实际项目中，需要根据类路径计算
        String path = classFile.getPath();
        String normalizedPath = path.replace(File.separatorChar, '.');

        // 移除".class"后缀
        if (normalizedPath.endsWith(".class")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 6);
        }

        // 提取包名和类名部分（这里需要根据实际项目调整）
        int startIndex = normalizedPath.indexOf("classes.") + 8;
        if (startIndex > 8) {
            return normalizedPath.substring(startIndex);
        }

        return classFile.getName().substring(0, classFile.getName().length() - 6);
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return annotationMetadata;
    }
}
