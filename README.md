# Mini-Spring Framework

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-red.svg)](https://www.oracle.com/java/)
[![Spring](https://img.shields.io/badge/Spring-6.x-green.svg)](https://spring.io/)

## 📖 项目简介

Mini-Spring 是一个简化版的 Spring Framework 实现，旨在帮助开发者深入理解 Spring 框架的核心原理和设计思想。通过精简的实现代码和详细的中文注释，让 Spring 源码学习不再困难！

## 🎯 项目价值

### 为什么要有 Mini-Spring？

- **源码复杂**：Spring 框架源码庞大复杂，初学者难以入手
- **设计精妙**：Spring 的设计模式和架构思想值得深入学习  
- **语言障碍**：英语注释和复杂术语阻碍理解
- **核心抽象**：过多的边缘逻辑掩盖了核心设计

### Mini-Spring 解决什么问题？

✅ **简化实现**：保留核心架构，删除冗余代码
✅ **中文注释**：每一行代码都有详细的中文解释
✅ **设计重现**：完整复现 Spring 的核心设计模式
✅ **学习路径**：从简单到复杂，循序渐进理解

## 🏗 核心架构

### 模块结构

```
mini-spring/
|-- mini-spring-beans/ # Bean管理模块
| 	|-- factory/ # Bean工厂
| 	|-- definition/ # Bean定义
|-- mini-spring-context/ # 应用上下文
|   |-- annotation/ # 注解支持
|   |-- support/ # 上下文支持
|-- mini-spring-core/ # 核心工具模块
| 	|-- io/ # 资源加载
| 	|-- env/ # 环境配置
| 	|-- util/ # 工具类
|-- mini-spring-aop/		# 持续更新...
|-- mini-spring-aspects/	# 持续更新...
|-- mini-spring-jdbc/		# 持续更新...
|-- mini-spring-tx/			# 持续更新...
|-- mini-spring-web/		# 持续更新...
|-- mini-spring-webmvc/		# 持续更新...
|-- mini-spring-boot/		# 主要实现starter功能 持续更新...
```

### 核心特性

#### 1. IOC 容器
```java
// 完全兼容 Spring 的使用方式
@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    public UserService userService() {
        return new UserService();
    }
}

ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
UserService userService = context.getBean(UserService.class);
```

#### 2. 完整的 Bean 生命周期

- BeanDefinition 注册
- 依赖注入
- 初始化回调
- 销毁流程

#### 3. 注解支持

- `@Component` - 组件扫描
- `@Autowired` - 自动装配
- `@Configuration` - 配置类
- `@Bean` - Bean 方法
- `@Scope` - 作用域
- `@Lazy` - 懒加载

#### 4. 环境配置

```java
// 多属性源支持（按优先级）
1. 系统属性 (System.getProperties())
2. 环境变量 (System.getenv())  
3. 配置文件 (application.properties)
```

## 🚀快速开始

### 环境要求

- JDK 17+
- Maven 3.6+

### 克隆项目

```bash
git clone https://github.com/csxyy/mini-spring.git
cd mini-spring
```



### 运行示例

```java
// 1. 定义配置类
@Configuration
@ComponentScan("com.example")
public class AppConfig {
}

// 2. 启动容器
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService service = context.getBean(MyService.class);
        service.execute();
    }
}
```



## 🔍 核心流程解析

### 容器启动流程

```java
new AnnotationConfigApplicationContext(AppConfig.class)
    │
    ├── 创建 BeanFactory (DefaultListableBeanFactory)
    ├── 创建 AnnotatedBeanDefinitionReader
    ├── 创建 ClassPathBeanDefinitionScanner  
    ├── 注册配置类
    └── refresh()
        │
        ├── prepareRefresh()          // 准备刷新
        ├── obtainFreshBeanFactory()   // 获取 BeanFactory
        ├── prepareBeanFactory()       // 准备 BeanFactory
        ├── invokeBeanFactoryPostProcessors()  // 处理配置类
        ├── registerBeanPostProcessors()       // 注册 Bean 后处理器
        └── finishBeanFactoryInitialization()  // 初始化单例 Bean
```



### Bean 创建流程

```java
getBean() → createBean() → doCreateBean()
    │
    ├── 实例化 (createBeanInstance)
    ├── 属性填充 (populateBean) 
    ├── 初始化 (initializeBean)
    │   ├── Aware 接口回调
    │   ├── BeanPostProcessor.pre
    │   ├── InitializingBean.afterPropertiesSet
    │   ├── init-method
    │   └── BeanPostProcessor.post
    └── 注册销毁逻辑
```



## 📚 学习指南

### 推荐学习顺序

1. **mini-spring-core** - 基础工具类（资源加载、环境配置）
2. **mini-spring-beans** - Bean 管理核心（BeanFactory、BeanDefinition）
3. **mini-spring-context** - 应用上下文（AnnotationConfigApplicationContext）

### 核心类解析

- `DefaultListableBeanFactory` - Bean 容器核心实现
- `AnnotationConfigApplicationContext` - 注解配置上下文
- `ConfigurationClassPostProcessor` - 配置类解析器
- `AutowiredAnnotationBeanPostProcessor` - 自动装配处理器

## 🛠 开发计划

### 已完成

- IOC 容器基础架构
- BeanDefinition 体系
- 注解配置支持
- 环境配置体系
- 单例 Bean 管理

### 进行中

- AOP 代理机制
- 事务管理
- Web MVC 支持
- 事件机制

### 计划中

- Spring Boot 自动配置
- 响应式编程支持
- 测试框架集成

## 🤝 贡献指南

我们欢迎任何形式的贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📞联系方式

- 📧 **邮箱**: xxx@xx.com
- 🐛 **Issues**: [项目 Issues](https://github.com/csxyy/mini-spring/issues)
- 💬 **讨论**: 通过 GitHub Discussions 或 Issues 进行技术交流

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](https://LICENSE) 文件了解详情

## 🙏 致谢

感谢 Spring Framework 团队提供的优秀设计和源码，本项目大量参考了 Spring 的设计思想和实现方式。

------

**如果这个项目对你有帮助，请给一个 ⭐️ 支持一下！**



