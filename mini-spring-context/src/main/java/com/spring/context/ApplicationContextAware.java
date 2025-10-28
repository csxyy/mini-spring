package com.spring.context;

import com.spring.beans.factory.Aware;
import com.spring.context.weaving.ApplicationContext;

/**
 * ClassName: ApplicationContextAware
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:27
 * @version: v1.0
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext);
}
