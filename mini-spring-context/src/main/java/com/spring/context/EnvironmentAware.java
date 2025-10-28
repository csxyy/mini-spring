package com.spring.context;

import com.spring.beans.factory.Aware;
import com.spring.core.env.Environment;

/**
 * ClassName: EnvironmentAware
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:23
 * @version: v1.0
 */
public interface EnvironmentAware extends Aware {

    void setEnvironment(Environment environment);
}
