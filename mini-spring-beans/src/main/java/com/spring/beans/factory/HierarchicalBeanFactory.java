package com.spring.beans.factory;

/**
 * ClassName: HierarchicalBeanFactory
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 15:23
 * @version: v1.0
 */
public interface HierarchicalBeanFactory extends BeanFactory {


    boolean containsLocalBean(String name);

}
