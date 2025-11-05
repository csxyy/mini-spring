package com.spring.core.io.support;

import com.spring.core.io.Resource;
import com.spring.core.io.ResourceLoader;

/**
 * ClassName: ResourcePatternResolver
 * Description: 扩展资源加载，支持模式匹配（如Ant风格）
 *
 * @Author: csx
 * @Create: 2025/11/4 - 22:03
 * @version: v1.0
 */
public interface ResourcePatternResolver extends ResourceLoader {
    /**
     * 批量获取资源
     * @param locationPattern
     * @return
     */
    Resource[] getResources(String locationPattern);
}
