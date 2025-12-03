package com.spring.core.type;

/**
 * ClassName: ClassMetadata
 * Description:
 *
 * 类元数据接口 - 对应Spring的ClassMetadata
 *
 * @Author: csx
 * @Create: 2025/12/2 - 23:55
 * @version: v1.0
 */
public interface ClassMetadata {

    /**
     * 获取类名
     */
    String getClassName();

    /**
     * 是否是接口
     */
    boolean isInterface();

    /**
     * 是否是抽象类
     */
    boolean isAbstract();

    /**
     * 是否是具体类（非接口且非抽象）
     */
    boolean isConcrete();

    /**
     * 是否是final类
     */
    boolean isFinal();

    /**
     * 是否有父类（不是Object类）
     */
    boolean hasSuperClass();

    /**
     * 获取父类名
     */
    String getSuperClassName();

    /**
     * 获取实现的接口
     */
    String[] getInterfaceNames();
}
