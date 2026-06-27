# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

这是一个基于 **JeecgBoot 3.4.4** 的企业级应用项目，是一个 Spring Boot 快速开发平台，包含自定义业务模块 (`mzx`)，用于项目管理、客户服务、账单管理和员工薪资管理。

## 构建与运行命令

```bash
# 构建项目
mvn clean install -DskipTests

# 开发模式运行（使用 application-dev.yml 配置）
mvn spring-boot:run -pl jeecg-module-system/jeecg-system-start

# 指定配置文件运行
mvn spring-boot:run -pl jeecg-module-system/jeecg-system-start -Dspring-boot.run.profiles=dev

# 打包部署
mvn clean package -pl jeecg-module-system/jeecg-system-start -am -DskipTests
```

应用运行端口：**8081**，上下文路径：**/xcom**

Swagger API 文档地址：`http://localhost:8081/xcom/doc.html`

## 数据库与基础设施

### MySQL 数据库连接（dev 环境）

```
地址：jdbc:mysql://192.168.1.4:3306/xcom-system
用户名：sa_xcom
密码：saxcom123456
驱动：com.mysql.cj.jdbc.Driver
```

连接参数：`characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true`

### Redis 配置（dev 环境）

```
地址：192.168.1.4
端口：6379
密码：xcom123456
数据库：0
```

### 其他说明

- **支持多种数据库**：MySQL、PostgreSQL、Oracle、SQL Server、达梦 DM8
- **动态数据源**：支持多数据源配置，使用 `dynamic-datasource-spring-boot-starter`

## 模块架构

```
jeecg-boot-parent (根 pom)
├── jeecg-boot-base-core     # 核心框架（通用工具类、基类、注解）
├── jeecg-module-system
│   ├── jeecg-system-api     # API 接口定义（local-api 子模块）
│   ├── jeecg-system-biz     # 业务逻辑实现
│   └── jeecg-system-start   # Spring Boot 启动入口
```

## 核心基类

- **JeecgController<T, S>** (`common/system/base/controller/JeecgController.java`)：控制器基类，提供 CRUD、Excel 导入导出功能
- **JeecgEntity**：实体基类，包含 id、createBy、createTime、updateBy、updateTime 字段
- **Result<T>** (`common/api/vo/Result.java`)：标准 API 响应包装器，使用 `Result.ok()`、`Result.error()`
- **JeecgService/JeecgServiceImpl**：服务层基类，继承 MyBatis-Plus 的 IService

## 业务模块（modules 包下）

- **system**：用户、角色、权限、部门、字典、日志管理
- **mzx**：自定义业务域 - 项目、客户、账单、薪资、工作日志
- **message**：消息通知和 WebSocket 通信
- **quartz**：定时任务管理
- **oss**：文件存储（本地/minio/阿里云 OSS）

## 新增功能开发

在 mzx 模块中新增实体的标准流程：

1. **实体类**：继承 `JeecgEntity`，放置于 `modules/mzx/entity/`
2. **Mapper**：接口放 `mapper/`，XML 放 `mapper/xml/`（MyBatis-Plus XML 路径：`classpath*:org/jeecg/modules/**/xml/*Mapper.xml`）
3. **Service**：接口 `IXxxService` 继承 `IService<Entity>`，实现类 `XxxServiceImpl` 继承 `ServiceImpl<Mapper, Entity>`
4. **Controller**：继承 `JeecgController<Entity, Service>` 或使用 `@RestController`

控制器示例：
```java
@RestController
@RequestMapping("/xxx")
public class XxxController extends JeecgController<Entity, IService<Entity>> {
    // CRUD 方法从 JeecgController 继承
    // 使用 QueryGenerator.initQueryWrapper() 构建查询条件
}
```

## 安全与认证

- 使用 **Apache Shiro** 进行认证/授权
- 使用 **JWT Token** 进行 API 访问控制
- 权限注解：`@RequiresPermissions("permission:code")`
- 获取当前登录用户：`(LoginUser) SecurityUtils.getSubject().getPrincipal()`

## API 文档

- 使用 **Knife4j**（Swagger UI），访问地址：`/doc.html`
- 控制器注解：`@Api(tags="...")`，方法注解：`@ApiOperation`

## 定时任务

- 基于 **Quartz** 调度（在 application-dev.yml 中配置）
- 任务类放于 `modules/mzx/job/`，如 `ProjectBillingJob`、`EmployeePayrollJob`
- 任务存储在数据库中，表前缀 `QRTZ_`

## Excel 导入导出

- 通过 JeecgController 的方法实现：
  - `exportXls()` - Excel 导出
  - `importExcel()` - Excel 导入
- 实体字段使用 `@Excel` 注解进行列映射

## 配置文件环境

- `dev`（默认）：开发环境 - application-dev.yml
- `test`：测试环境
- `prod`：生产环境
- `dm8`：达梦数据库专用

## 测试

测试类位于 `jeecg-system-start/src/test/java/`。构建时默认跳过测试（`<skipTests>true</skipTests>`）。

运行测试：
```bash
mvn test -pl jeecg-module-system/jeecg-system-start -DskipTests=false
```