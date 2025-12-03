package com.spring.core.type;

import java.lang.reflect.Modifier;

/**
 * ClassName: StandardClassMetadata
 * Description:
 *
 * 简单的类元数据实现
 *
 * @Author: csx
 * @Create: 2025/12/3 - 0:00
 * @version: v1.0
 */
public class StandardClassMetadata implements ClassMetadata {
    private final Class<?> clazz;

    public StandardClassMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getClassName() {
        return clazz.getName();
    }

    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    @Override
    public boolean isConcrete() {
        return !isInterface() && !isAbstract();
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(clazz.getModifiers());
    }

    @Override
    public boolean hasSuperClass() {
        return clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class);
    }

    @Override
    public String getSuperClassName() {
        Class<?> superClass = clazz.getSuperclass();
        return superClass != null ? superClass.getName() : null;
    }

    @Override
    public String[] getInterfaceNames() {
        Class<?>[] interfaces = clazz.getInterfaces();
        String[] interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceNames[i] = interfaces[i].getName();
        }
        return interfaceNames;
    }
}
