package com.spring.beans.factory.config;

import lombok.Data;

/**
 * ClassName: BeanDefinitionHolder
 * Description: Bean定义持有者
 *
 * @Author: csx
 * @Create: 2025/11/12 - 21:49
 * @version: v1.0
 */
@Data
public class BeanDefinitionHolder {
    private final BeanDefinition beanDefinition;

    private final String beanName;

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
    }
}
