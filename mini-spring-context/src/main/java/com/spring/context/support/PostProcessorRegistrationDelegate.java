package com.spring.context.support;

import com.spring.beans.factory.config.BeanFactoryPostProcessor;
import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName: PostProcessorRegistrationDelegate
 * Description: 后置处理器注册委托 - 负责调用BeanFactoryPostProcessor
 *
 * @Author: csx
 * @Create: 2025/11/12 - 9:33
 * @version: v1.0
 */
@Slf4j
public class PostProcessorRegistrationDelegate {

    /**
     * 这里完成了配置类解析、包扫描、Bean定义注册等核心功能
     * 可以直接利用ApplicationContext来注册BeanFactoryPostProcessor
     * PostProcessorRegistrationDelegate工具类 - 可以注册新的Bean定义
     */
    public static void invokeBeanFactoryPostProcessors(
            ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        /**
         * Spring 的执行顺序（极其复杂）：
         *  1. 先处理 BeanDefinitionRegistryPostProcessor（按优先级）
         *      - 实现 PriorityOrdered 接口的
         *      - 实现 Ordered 接口的
         *      - 普通的
         *  2. 再处理 BeanFactoryPostProcessor（按优先级）
         *      - 实现 PriorityOrdered 接口的
         *      - 实现 Ordered 接口的
         *      - 普通的
         * 我们先实现基础版本，重点展示核心流程，而不是边界情况，后续会逐步添加：
         *  - 优先级支持（PriorityOrdered, Ordered）
         *  - 更精确的类型查找
         *  - 异常处理和状态管理
         *  - 父子容器支持
         */

        log.info("开始调用BeanFactoryPostProcessor");

        // 1. 先调用BeanDefinitionRegistryPostProcessor（优先级最高）
        Set<String> processedBeans = new HashSet<>();   // 去重
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        if (beanFactory instanceof BeanDefinitionRegistry registry) {
            // 1.1 首先处理手动注册的BeanDefinitionRegistryPostProcessor
            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor registryProcessor) {
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                }
            }

            // 1.2 然后处理从BeanFactory中获取的BeanDefinitionRegistryPostProcessor
            String[] postProcessorNames = beanFactory.getBeanNamesForType(
                    BeanDefinitionRegistryPostProcessor.class, true, false);

            for (String ppName : postProcessorNames) {
                if (!processedBeans.contains(ppName)) {
                    BeanDefinitionRegistryPostProcessor pp = beanFactory.getBean(ppName,
                            BeanDefinitionRegistryPostProcessor.class);
                    pp.postProcessBeanDefinitionRegistry(registry);
                    processedBeans.add(ppName);
                    registryProcessors.add(pp);
                }
            }
        }

        // 2. 调用BeanFactoryPostProcessor
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

        // 2.1 首先处理手动注册的BeanFactoryPostProcessor
        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            if (!(postProcessor instanceof BeanDefinitionRegistryPostProcessor)) {
                postProcessor.postProcessBeanFactory(beanFactory);
                regularPostProcessors.add(postProcessor);
            }
        }

        // 2.2 然后处理从BeanFactory中获取的BeanFactoryPostProcessor
        String[] regularPostProcessorNames = beanFactory.getBeanNamesForType(
                BeanFactoryPostProcessor.class, true, false);

        for (String ppName : regularPostProcessorNames) {
            if (!processedBeans.contains(ppName)) { // 防重复调用
                BeanFactoryPostProcessor pp = beanFactory.getBean(ppName,
                        BeanFactoryPostProcessor.class);
                pp.postProcessBeanFactory(beanFactory);
                processedBeans.add(ppName);
                regularPostProcessors.add(pp);
            }
        }

        log.info("BeanFactoryPostProcessor调用完成");
    }
}
