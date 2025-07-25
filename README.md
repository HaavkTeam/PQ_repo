# Popquiz项目介绍

## 项目详情
本项目是基于Vue+Springboot+Mysql+python实现的popquiz系统，把各种模态的输入变为文字，保存到数据库，并分析数据库里面的 ”内容“， 把它们变成各种各样的测试题目（目前是 选择题），把题目通过 小程序/app 和用户交互， 用户信息管理， 收集反馈/判题/统计数据， 发给用户和内容的创造者 （老师， 演讲者，组织者，等）。

## 技术栈
- 前端核心框架：Vue
- 后端核心框架：Spring Boot
- 持久层：Mybatis-plus
- 数据库：MySQL

## 后端推荐环境要求
- Java 17
- Maven 3.6.3
- MySQL 8.3
- IDE：IntelliJ IDEA
- NODEJS：v22.17.0
- npm：10.9.2
- vue：@vue/cli 5.0.8
## 安装与部署

### 1.克隆项目
```bash
git clone https://github.com/HaavkTeam/PQ_repo.git 

cd PQ_repo 
```

### 2.配置数据库
创建数据库:
```sql
CREATE DATABASE [数据库名] CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改配置文件：Backend/src/main/resources/application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/[数据库名]?useSSL=false
spring.datasource.username=[用户名]
spring.datasource.password=[密码]
```

导入数据库文件：popquizdb.sql

### 3.启动后端服务
运行 Backend/src/main/java/org/pqproject/backend/BackendApplication.java

### 4.启动前端服务
使用终端，在前端项目下输入npm run dev，然后输入o就会跳出页面

## 项目结构
### 后端项目结构
```plaintext
Backend/
├── .idea/                # IDEA 工程配置目录，存储代码索引、运行配置等
├── .mvn/                 # Maven 包装器相关文件（可能用于无本地 Maven 时构建）
├── src/                  # 源代码根目录
│   ├── main/             # 主代码、资源目录
│   │   ├── java/         # Java 源代码目录
│   │   │   └── org.pqproject.backend/ # 根包路径
│   │   │       ├── controller/  # 控制器层，处理请求和响应
│   │   │       ├── mapper/      # 数据访问层，操作数据库（MyBatis-Plus 等）
│   │   │       ├── pojo/        # 实体类，映射数据库表结构
│   │   │       ├── service/     # 业务逻辑层，封装业务处理
│   │   │       └── BackendApplication.java # Spring Boot 启动类
│   │   └── resources/    # 资源文件目录
│   │       ├── static/   # 静态资源目录（如无需前端独立部署，可放前端静态文件 ）
│   │       ├── templates/ # 模板文件目录（如使用 Thymeleaf 等模板引擎时用）
│   │       └── application.properties # Spring Boot 核心配置文件，配数据库、端口等
│   └── test/             # 测试代码目录（当前截图未展开，可放单元测试等 ）
├── target/               # Maven 构建输出目录，存放编译后的 class、打包的 jar 等
├── .gitattributes        # Git 属性配置，定义文件差异对比、二进制文件处理规则
├── .gitignore            # Git 忽略规则，指定无需提交到仓库的文件/目录
├── HELP.md               # 项目帮助文档，可说明启动、部署等基础指引
├── mvnw                  # Maven 包装器脚本（Linux/Mac 下），无本地 Maven 时执行构建
├── mvnw.cmd              # Maven 包装器脚本（Windows 下）
├── pom.xml               # Maven 项目核心配置，定义依赖、构建插件、项目信息等
├── External Libraries    # IDE 显示的外部依赖库（非实际项目文件，是引用的 Jar 包集合 ）
└── Scratches and Consoles # IDEA 的临时代码片段、控制台记录等（开发临时使用 ）
```
### 前端项目结构
```plaintext
├── .editorconfig          # 编辑器配置
├── .gitattributes         # Git属性配置
├── .gitignore             # Git忽略文件配置
├── .prettierrc.json       # Prettier代码格式化配置
├── .vscode/               # VS Code编辑器配置
├── README.md              # 项目说明文档
├── e2e/                   # 端到端测试目录
├── env.d.ts               # 环境变量类型声明
├── eslint.config.ts       # ESLint配置
├── index.html             # 入口HTML文件
├── package.json           # 项目依赖和脚本配置
├── playwright.config.ts   # Playwright测试配置
├── public/                # 静态资源目录
├── src/                   # 源代码目录
├── tsconfig*.json         # TypeScript配置文件
└── vite.config.ts         # Vite构建工具配置
├── App.vue                # 应用根组件
├── api/                   # API接口目录
│   ├── auth.ts            # 认证相关API
│   ├── comment.ts         # 评论相关API
│   ├── config.ts          # API配置
│   ├── question.ts        # 问题相关API
│   └── speech.ts          # 演讲相关API
├── assets/                # 静态资源
│   ├── base.css           # 基础样式
│   ├── logo.svg           # Logo图片
│   └── main.css           # 主样式文件
├── components/            # 公共组件
│   ├── HelloWorld.vue     # 示例组件
│   ├── TheWelcome.vue     # 欢迎组件
│   ├── WelcomeItem.vue    # 欢迎项组件
│   ├── __tests__/         # 组件测试
│   └── icons/             # 图标组件
├── main.ts                # 应用入口文件
├── router/                # 路由配置
│   └── index.ts           # 路由定义
├── stores/                # 状态管理
│   └── counter.ts         # 计数器状态示例
└── views/                 # 视图组件
    ├── AboutView.vue      # 关于页面
    ├── HomeView.vue       # 首页
    ├── auth/              # 认证相关页面
    ├── organizer/         # 组织者相关页面
    ├── student/           # 学生相关页面
    └── teacher/           # 教师相关页面
├── auth/                  # 认证相关页面
│   ├── LoginView.vue      # 登录页面
│   └── RegisterView.vue   # 注册页面
├── organizer/             # 组织者相关页面
│   ├── DashboardView.vue  # 组织者仪表盘
│   └── LectureView.vue    # 组织者演讲页面
├── student/               # 学生相关页面
│   ├── DashboardView.vue  # 学生仪表盘
│   ├── QuizView.vue       # 学生测验页面
│   └── TestListView.vue   # 学生测试列表页面
└── teacher/               # 教师相关页面
    ├── DashboardView.vue  # 教师仪表盘
    └── LectureView.vue    # 教师演讲页面
```
## 贡献指南

1. **Fork 本仓库**  
   点击仓库页面的 `Fork` 按钮，将项目复制到你的个人账号下。

2. **创建特性分支**  
   克隆 Fork 后的仓库到本地，并创建新的特性分支：  
   ```bash
   git clone https://github.com/你的用户名/PQ_repo.git
   cd PQ_repo
   git checkout -b feature/your-feature  # 例如：feature/add-question-type
   ```

3. **提交代码**  
   在分支上开发完成后，提交修改并添加清晰的提交信息：  
   ```bash
   git add .
   git commit -m 'Add some feature'  # 例如：'添加多选题生成功能'
   ```

4. **推送分支**  
   将本地分支推送到你的远程仓库：  
   ```bash
   git push origin feature/your-feature
   ```

5. **提交 Pull Request**  
   回到原仓库页面，点击 `Compare & pull request`，填写修改说明并提交，等待审核合并。

> 注意：  
> - 提交前请确保代码符合项目编码规范，并通过本地测试。  
> - 大型功能建议先在 Issues 中讨论方案，避免重复开发。
