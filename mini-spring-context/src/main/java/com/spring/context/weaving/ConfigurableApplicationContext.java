package com.spring.context.weaving;

import com.spring.beans.factory.config.ConfigurableListableBeanFactory;
import com.spring.core.env.ConfigurableEnvironment;

/**
 * ClassName: ConfigurableApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 15:26
 * @version: v1.0
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    // 常量定义
    String ENVIRONMENT_BEAN_NAME = "environment";
    String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
    String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


    void refresh();

    /** 以可配置的形式返回此应用程序上下文的 Environment，以便进一步自定义。 */
    @Override
    ConfigurableEnvironment getEnvironment();

    ConfigurableListableBeanFactory getBeanFactory();
}
