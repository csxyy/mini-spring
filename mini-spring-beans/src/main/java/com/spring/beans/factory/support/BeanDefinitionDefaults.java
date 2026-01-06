package com.spring.beans.factory.support;

import lombok.Data;

/**
 * ClassName: BeanDefinitionDefaults
 * Description:
 *
 * BeanDefinition默认属性：通过doScan方法中的后置处理器设置
 *
 * @Author: csx
 * @Create: 2025/12/4 - 23:14
 * @version: v1.0
 */
@Data
public class BeanDefinitionDefaults {
    private Boolean lazyInit;      // 懒加载

    private int autowireMode = 0; // @Autowired已经替代了（不用管）

    private int dependencyCheck = 0;    // Spring会自动处理依赖注入的检查，所以不需要设置（不用管）

    private String initMethodName;         // @PostConstruct已经替代了（不用管）

    private String destroyMethodName;      // @PreDestroy已经替代了（不用管）
}











