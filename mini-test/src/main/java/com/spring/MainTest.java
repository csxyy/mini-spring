package com.spring;

import com.spring.config.MyConfig;
import com.spring.context.annotation.AnnotationConfigApplicationContext;
import com.spring.context.annotation.BeanAnnotationHelper;
import com.spring.context.annotation.ConfigurationClassBeanDefinitionReader;

import java.lang.reflect.Method;

/**
 * ClassName: Test
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:52
 * @version: v1.0
 */
public class MainTest {
    public static void main(String[] args) throws NoSuchMethodException {
        AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(MyConfig.class);


        System.out.println(ioc.getBean("userName1"));
        System.out.println(ioc.getBean("userName1"));
        System.out.println(ioc.getBean("userName2"));
        System.out.println(ioc.getBean("userName2"));
    }
}
