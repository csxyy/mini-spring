package com.spring.context.weaving;

/**
 * ClassName: ConfigurableApplicationContext
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/25 - 15:26
 * @version: v1.0
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    void refresh();

    /** 以可配置的形式返回此应用程序上下文的 Environment，以便进一步自定义。 */
//    @Override
//    ConfigurableEnvironment getEnvironment();
}
