# Popquiz项目介绍
## 项目详情
本项目是基于Vue+Springboot+Mysql+python实现的popquiz系统，把各种模态的输入变为文字，保存到数据库，并分析数据库里面的 ”内容“， 把它们变成各种各样的测试题目（目前是 选择题），把题目通过 小程序/app 和用户交互， 用户信息管理， 收集反馈/判题/统计数据， 发给用户和内容的创造者 （老师， 演讲者，组织者，等）。
## 技术栈
* 前端核心框架：Vue
* 后端核心框架：Spring Boot
* 持久层：Mybatis-plus
* 数据库：MySQL
## 后端推荐环境要求
* Java 17
* Maven 3.6.3
* MySQL 8.3
* IDE：IntelliJ IDEA
## 安装与部署
### 1.克隆项目
git clone https://github.com/HaavkTeam/PQ_repo.git 

cd PQ_repo 

### 2.配置数据库
创建数据库:CREATE DATABASE [数据库名] CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

修改配置文件：Backend/src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/[数据库名]?useSSL=false
spring.datasource.username=[用户名]
spring.datasource.password=[密码]

导入数据库文件：
### 3.启动服务
运行 Backend/src/main/java/org/pqproject/backend/BackendApplication.java
