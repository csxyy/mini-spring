package com.spring.context;

import com.spring.beans.factory.Aware;

/**
 * ClassName: ApplicationEventPublisherAware
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 14:25
 * @version: v1.0
 */
public interface ApplicationEventPublisherAware extends Aware {
    void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);
}
