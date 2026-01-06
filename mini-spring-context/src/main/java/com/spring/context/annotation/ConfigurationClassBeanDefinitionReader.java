package com.spring.context.annotation;

import com.spring.beans.factory.annotation.AnnotatedBeanDefinition;
import com.spring.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.spring.beans.factory.config.BeanDefinition;
import com.spring.beans.factory.support.AbstractBeanDefinition;
import com.spring.beans.factory.support.BeanDefinitionRegistry;
import com.spring.beans.factory.support.RootBeanDefinition;
import com.spring.core.type.AnnotationMetadata;
import com.spring.core.type.MethodMetadata;
import com.spring.core.type.StandardAnnotationMetadata;
import com.spring.core.type.StandardMethodMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: ConfigurationClassBeanDefinitionReader
 * Description: 配置类Bean定义读取器
 *
 * 负责将ConfigurationClass中定义的Bean注册为BeanDefinition
 *
 * Spring设计思想：
 *  这个类专门负责"注册"阶段，将parse阶段收集的信息转换为BeanDefinition
 *  主要处理@Bean方法、@Import引入的类等
 *
 * @Author: csx
 * @Create: 2025/11/12 - 23:06
 * @version: v1.0
 */
@Slf4j
public class ConfigurationClassBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    public ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 加载Bean定义
     */
    public void loadBeanDefinitions(Set<ConfigurationClass> configClasses) {
        log.debug("ConfigurationClassBeanDefinitionReader.loadBeanDefinitions()开始");

        for (ConfigurationClass configClass : configClasses) {
            log.debug("从ConfigurationClass加载BeanDefinitions: {}", configClass.getMetadata().getClassName());

            // 处理@Import引入的类 -> 注册为BeanDefinition
            registerBeanDefinitionForImportedConfigurationClass(configClass);

            // TODO: 处理@Bean方法 - 将@Bean方法转换为BeanDefinition并注册
            // loadBeanDefinitionsForBeanMethods(configClass);
            for (BeanMethod beanMethod : configClass.getBeanMethods()) {
                loadBeanDefinitionsForBeanMethod(beanMethod);
            }

            // TODO: 处理ImportBeanDefinitionRegistrar（@EnableXxx）
            // loadBeanDefinitionsFromRegistrars(configClass);

            log.debug("ConfigurationClass BeanDefinitions加载完成: {}", configClass.getMetadata().getClassName());
        }

        log.debug("ConfigurationClassBeanDefinitionReader.loadBeanDefinitions()完成");
    }

    /**
     * 注册被导入的配置类本身的BeanDefinition
     * @Import(OrderService.class) ---> 将OrderService注册为BeanDefinition
     */
    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        AnnotationMetadata metadata = configClass.getMetadata();
        String className = metadata.getClassName();

        log.debug("处理导入的配置类: {}", className);

        // 检查是否有@Import注解（包括元注解）
        if (metadata.isAnnotated("com.spring.context.annotation.Import")) {
            log.debug("类 {} 有@Import注解（直接或通过元注解）", className);

            // 获取@Import注解的属性值
            Map<String, Object> importAttributes = metadata.getAnnotationAttributes(
                    "com.spring.context.annotation.Import");

            if (importAttributes != null && importAttributes.containsKey("value")) {
                Object value = importAttributes.get("value");
                if (value instanceof Class[]) {
                    Class<?>[] importClasses = (Class<?>[]) value;
                    log.debug("@Import导入 {} 个类", importClasses.length);

                    for (Class<?> importClass : importClasses) {
                        String importClassName = importClass.getName();
                        log.debug("注册导入的类: {}", importClassName);

                        // 创建BeanDefinition
                        AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(importClass);

                        // 处理通用注解（@Lazy, @Scope等）
                        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef);

                        // Spring这里是用Bean名称生成器生成的，其实就是类的全限名 importClassName

                        // 检查是否可注册
                        registry.registerBeanDefinition(importClassName, configBeanDef);
                        log.debug("成功注册导入的BeanDefinition: {} -> {}", importClassName, importClassName);
                    }
                } else {
                    log.warn("@Import注解的value属性类型不支持: {}", value.getClass());
                }
            }
        }
    }

    /**
     * 为@Bean方法加载BeanDefinition - 对应Spring的loadBeanDefinitionsForBeanMethod方法
     * 这是@Bean方法转换为BeanDefinition的核心逻辑
     */
    private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
        log.debug("开始处理@Bean方法: {} -> {}",
                beanMethod.getMethodName(), beanMethod.getReturnTypeName());

        ConfigurationClass configClass = beanMethod.getConfigurationClass();
        MethodMetadata metadata = beanMethod.getMetadata();
        String methodName = metadata.getMethodName();

        // 第一步：条件注解检查 - 占时不实现，写注释留扩展
        // Spring支持@Conditional注解，用于条件化注册Bean
        // 例如：@ConditionalOnClass, @ConditionalOnProperty等
        // 我们这里先跳过条件检查，后续可以实现ConditionEvaluator
        log.debug("条件注解检查暂未实现，跳过");

        // 第二步：获取 @Bean 注解属性
        Map<String, Object> beanAttributes = beanMethod.getBeanAttributes();
        if (beanAttributes == null) {
            log.warn("@Bean方法 {} 没有注解属性，跳过", methodName);
            return;
        }

        // 第三步：Bean名称处理
        String[] explicitNames = (String[]) beanAttributes.get("beanNames");
        String beanName;
        // 优先使用@Bean注解指定的名称
        if (explicitNames != null && explicitNames.length > 0) {
            beanName = explicitNames[0];
            log.debug("@Bean方法 {} 使用指定名称: {}",
                    methodName, Arrays.toString(explicitNames));
        } else {
            beanName = methodName;
            log.debug("@Bean方法 {} 使用默认名称: {}", methodName, beanName);
        }

        // 第四步：检查是否被覆盖
        if (isOverriddenByExistingDefinition(beanMethod, beanName)) {
            log.debug("@Bean方法 {} 已被现有定义覆盖，跳过", methodName);
            return;
        }

        // 第五步：创建 ConfigurationClassBeanDefinition
        ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass, metadata, beanName);
        beanDef.setSource(beanMethod); // 设置源信息（用于调试）

        // 第六步：设置工厂方法（区分静态方法和实例方法）
        if (metadata.isStatic()) {
            // 静态工厂方法：factoryClass + factoryMethod
            if (configClass.getMetadata() instanceof StandardAnnotationMetadata sam) {
                beanDef.setBeanClass(sam.getIntrospectedClass());
            } else {
                beanDef.setBeanClassName(configClass.getMetadata().getClassName());
            }
            beanDef.setFactoryMethodName(methodName);
            log.debug("@Bean方法 {} 是静态方法，使用静态工厂", beanMethod.getMethodName());
        } else {
            // 实例工厂方法：factoryBean + factoryMethod
            // 工厂Bean是配置类本身
            beanDef.setFactoryBeanName(configClass.getBeanName());
            beanDef.setFactoryMethodName(methodName);
            log.debug("@Bean方法 {} 是实例方法，使用工厂Bean: {}",
                    methodName, configClass.getBeanName());
        }

        // 第七步：解析工厂方法（优化）
        if (metadata instanceof StandardMethodMetadata smm &&
                configClass.getMetadata() instanceof StandardAnnotationMetadata sam) {
            // 简化实现：在创建Bean时解析工厂方法
            // Spring原版会使用MethodOverrides等机制处理重写
            // 我们这里标记方法需要被解析
            beanDef.setResolvedFactoryMethod(beanMethod.getMethod());
            log.debug("标记@Bean方法 {} 的工厂方法需要解析", methodName);
        }


        // 第八步：设置自动装配模式
        beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);
        log.debug("设置@Bean方法 {} 的自动装配模式: AUTOWIRE_CONSTRUCTOR, autowireMode: {}",
                beanMethod.getMethodName(), beanDef.getAutowireMode());

        // 第九步：处理 @Bean 注解的其他属性
        boolean autowireCandidate = beanAttributes.containsKey("autowireCandidate");
        if (!autowireCandidate) {
            beanDef.setAutowireCandidate(false);	// 不作为自动装配候选
        }

        String initMethodName = (String) beanAttributes.get("initMethod");
        if (initMethodName != null && !initMethodName.isEmpty()) {
            beanDef.setInitMethodName(initMethodName);
            log.debug("设置@Bean方法 {} 的初始化方法: {}", methodName, initMethodName);
        }

        String destroyMethodName = (String) beanAttributes.get("destroyMethod");
        if (destroyMethodName != null && !destroyMethodName.isEmpty()) {
            beanDef.setDestroyMethodName(destroyMethodName);
            log.debug("设置@Bean方法 {} 的销毁方法: {}", methodName, destroyMethodName);
        }

        // 第十步：处理作用域和代理（代理先占时不实现）
        Scope scopeAnnotation = beanMethod.getMethod().getAnnotation(Scope.class);
        if (scopeAnnotation != null) {
            // 设置作用域
            String scope = scopeAnnotation.value();
            if (!scope.isEmpty()) {
                beanDef.setScope(scope);
                log.debug("设置@Bean方法 {} 的作用域: {}", beanMethod.getMethodName(), scope);
            }

            // 代理模式处理（涉及AOP，暂时不实现）
            // ScopedProxyMode proxyMode = scopeAnnotation.proxyMode();
            // 处理代理逻辑...
        }

        // 第十一步：注册BeanDefinition
        this.registry.registerBeanDefinition(beanName, beanDef);

        log.debug("@Bean方法 {} 处理完成", beanMethod.getMethodName());
    }

    /**
     * 检查是否被现有定义覆盖
     */
    protected boolean isOverriddenByExistingDefinition(BeanMethod beanMethod, String beanName) {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return false;
        }

        BeanDefinition existingDef = registry.getBeanDefinition(beanName);

        // 检查是否来自同一个配置类
        // Spring原版会检查是否是同一个@Bean方法的重写（比如在子类中）
        // 我们简化实现：如果已存在，则认为被覆盖
        log.debug("Bean名称 {} 已存在，@Bean方法 {} 将被覆盖",
                beanName, beanMethod.getMethodName());
        return true;
    }

    /**
     * 配置类BeanDefinition - 对应Spring的ConfigurationClassBeanDefinition
     * 专门用于存储@Bean方法相关的Bean定义信息
     */
    private static class ConfigurationClassBeanDefinition extends RootBeanDefinition implements AnnotatedBeanDefinition {

        private final AnnotationMetadata annotationMetadata; // 配置类本身的元数据（Config类的注解）

        private final MethodMetadata factoryMethodMetadata; // 工厂方法的元数据（userService方法的注解）

        private final String derivedBeanName;

        public ConfigurationClassBeanDefinition(
                ConfigurationClass configClass, MethodMetadata beanMethodMetadata, String derivedBeanName) {

            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = beanMethodMetadata;
            this.derivedBeanName = derivedBeanName;
        }

        public ConfigurationClassBeanDefinition(RootBeanDefinition original,
                                                ConfigurationClass configClass, MethodMetadata beanMethodMetadata, String derivedBeanName) {

            super(original);
            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = beanMethodMetadata;
            this.derivedBeanName = derivedBeanName;
        }

        private ConfigurationClassBeanDefinition(ConfigurationClassBeanDefinition original) {
            super(original);
            this.annotationMetadata = original.annotationMetadata;
            this.factoryMethodMetadata = original.factoryMethodMetadata;
            this.derivedBeanName = original.derivedBeanName;
        }

        @Override
        public AnnotationMetadata getMetadata() {
            return this.annotationMetadata;
        }

        @Override
        public MethodMetadata getFactoryMethodMetadata() {
            return this.factoryMethodMetadata;
        }

        @Override
        public boolean isFactoryMethod(Method candidate) {
            return (super.isFactoryMethod(candidate) && BeanAnnotationHelper.isBeanAnnotated(candidate) &&
                    BeanAnnotationHelper.determineBeanNameFor(candidate).equals(this.derivedBeanName));
        }

        @Override
        public ConfigurationClassBeanDefinition cloneBeanDefinition() {
            return new ConfigurationClassBeanDefinition(this);
        }
    }
}
