package com.spring;

import com.spring.config.MyConfig;
import com.spring.context.annotation.AnnotationConfigApplicationContext;

/**
 * ClassName: Test
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/27 - 22:52
 * @version: v1.0
 */
public class MainTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(MyConfig.class);

        // @Component、@Controller、@Service、@Repository
        System.out.println(ioc.getBean("userController"));
        System.out.println(ioc.getBean("userService"));
        System.out.println(ioc.getBean("userDao"));

        // 懒加载 @Lazy
        System.out.println(ioc.getBean("lazyUser"));

        // 通过 @Scope("prototype") 实现多例Bean
        System.out.println(ioc.getBean("prototypeUser"));
        System.out.println(ioc.getBean("prototypeUser"));

        // 通过 BeanFactoryPostProcessor 修改 BeanDefinition 实现多例Bean
        System.out.println(ioc.getBean("user"));
        System.out.println(ioc.getBean("user"));
    }
}
