package com.spring.util;

/**
 * ClassName: ObjectUtils
 * Description:
 *
 * @Author: csx
 * @Create: 2025/12/4 - 23:43
 * @version: v1.0
 */
public abstract class ObjectUtils {

    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

}
