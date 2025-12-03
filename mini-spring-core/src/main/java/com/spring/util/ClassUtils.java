package com.spring.util;

/**
 * ClassName: ClassUtils
 * Description:
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:52
 * @version: v1.0
 */
/**
 * 类工具类 - 对应Spring的ClassUtils
 */
public class ClassUtils {

    /**
     * CGLIB类分隔符 - 对应Spring的ClassUtils.CGLIB_CLASS_SEPARATOR
     * 用于过滤CGLIB生成的代理类
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 默认的类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Exception ex) {
            // 忽略
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Exception ex) {
                    // 忽略
                }
            }
        }
        return cl;
    }

    /**
     * 检查是否是CGLIB代理类名
     */
    public static boolean isCglibProxyClassName(String className) {
        return className != null && className.contains(CGLIB_CLASS_SEPARATOR);
    }
}
