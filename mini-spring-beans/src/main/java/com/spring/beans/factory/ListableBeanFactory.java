package com.spring.beans.factory;

/**
 * ClassName: ListableBeanFactory
 * Description: 可列出所有Bean定义的BeanFactory - 扩展了枚举Bean的能力
 *
 * @Author: csx
 * @Create: 2025/10/28 - 9:11
 * @version: v1.0
 */
public interface ListableBeanFactory extends BeanFactory {
    /**
     * 获取所有Bean定义的名称
     * @return Bean名称数组
     */
    String[] getBeanDefinitionNames();

    /**
     * 获取Bean定义的数量
     * @return Bean定义数量
     */
    int getBeanDefinitionCount();

    /**
     * 检查是否包含指定名称的Bean定义
     * @param beanName Bean名称
     * @return 是否包含
     */
    boolean containsBeanDefinition(String beanName);
}
