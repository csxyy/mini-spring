package com.spring.context;

/**
 * ClassName: ApplicationEventPublisher
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:26
 * @version: v1.0
 */
@FunctionalInterface
public interface ApplicationEventPublisher {

    void publishEvent(Object event);
}
