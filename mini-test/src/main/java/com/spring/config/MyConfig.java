package com.spring.config;

import com.spring.bean.User;
import com.spring.context.annotation.*;
import com.spring.mvc.OrderServer;
import com.spring.mvc.UserDao;
import com.spring.mvc.UserService;
import com.spring.stereotype.Component;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * ClassName: MyConfig
 * Description:
 *
 * @Author: csx
 * @Create: 2025/10/28 - 8:46
 * @version: v1.0
 */
@ComponentScan("com.spring.config")
public class MyConfig {

    @Bean("userName1")
    public User user1() {
        return new User();
    }

    @Bean("userName2")
    public static User user2() {
        return new User();
    }

//    @Bean({"dataSource", "ds"})
//    @Scope("prototype")
//    public UserDao dataSource() {
//        return new UserDao();
//    }
//
//    @Bean(initMethod = "init", destroyMethod = "cleanup")
//    @Lazy
//    public OrderServer connectionPool() {
//        return new OrderServer();
//    }
//
//    @Bean(autowireCandidate = false)
//    @Primary
//    public UserService specialService() {
//        return new UserService();
//    }
}
