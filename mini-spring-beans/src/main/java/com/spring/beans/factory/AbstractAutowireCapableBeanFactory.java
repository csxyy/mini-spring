package com.spring.beans.factory;

import com.spring.beans.factory.config.AutowireCapableBeanFactory;
import com.spring.beans.factory.support.AbstractBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * ClassName: AbstractAutowireCapableBeanFactory
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 15:00
 * @version: v1.0
 */
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
                                        implements AutowireCapableBeanFactory {


}
