package com.spring.beans.factory;

/**
 * ClassName: BeanFactoryUtils
 * Description: FactoryBean工具类
 *
 * @Author: csx
 * @Create: 2025/11/10 - 9:41
 * @version: v1.0
 */
public abstract class BeanFactoryUtils {
    /**
     * 检查是否是FactoryBean引用（name以&开头）
     */
    public static boolean isFactoryDereference(String name) {
        return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
    }

    /**
     * 转换FactoryBean名称（去除&前缀）
     */
    public static String transformedBeanName(String name) {
        if (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            return name.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
        }
        return name;
    }
}
