# 用短链接服务学习 Java 后端

这是一份要你亲手完成的教程，而不是一套可直接交付的代码。默认你会前端开发、没有 Spring Boot 经验。每章只给出目标、约束与验收方式；先自己实现，卡住时再回看提示。建议每完成一章做一次 Git 提交，提交信息写下你新理解的概念。

## 开始前

项目已是一个可启动的 Spring Boot 骨架。它需要 JDK 17；无需安装 Maven，项目自带 Maven Wrapper。

```bash
cd short-link-service
./mvnw spring-boot:run
```

浏览器访问 `http://localhost:8080` 得到 404 是正常的：我们还没有定义接口。

## 第 0 章：先认识 Spring Boot

Spring Boot 是一个帮助你快速启动 Java Web 服务的框架。它做了两件对初学者最重要的事：自动配置 Web 服务器，以及按照注解把 HTTP 请求转发到你的 Java 方法。

先看 `src/main/java/com/example/shortlink/ShortLinkServiceApplication.java`。不要修改它，先理解：

- `public static void main` 是 Java 应用的入口，类似 Node.js 文件最顶层开始执行的位置。
- `SpringApplication.run(...)` 启动 Spring 容器和内嵌 Web 服务器。无需单独安装 Tomcat。
- `@SpringBootApplication` 告诉 Spring 从当前包开始查找带有框架注解的类。
- Maven 是 Java 的依赖与构建工具，`pom.xml` 类似 `package.json`，但更偏向构建配置。

启动项目：

```bash
./mvnw spring-boot:run
```

看到包含 `Started ShortLinkServiceApplication` 的日志，就代表服务启动成功。按 `Ctrl+C` 停止它。`./mvnw` 是项目自带的 Maven；不需要全局安装 Maven。

此时访问 `http://localhost:8080` 得到 404 是正常的：服务器已启动，但还没有路由。

前端类比：`@RestController` 类似一个专门输出 JSON 的路由模块；`@GetMapping` / `@PostMapping` 类似 Express 的 `app.get` / `app.post`。不同点是 Java 在编译期把输入和返回值的结构固定下来。

## 第 1 章：写出第一个接口

目标：在 `controller` 包创建 `HealthController`，实现 `GET /api/health`，返回 JSON `{ "status": "ok" }`。

你会学到：包、类、方法、注解、Java `record`，以及 Spring 如何扫描并注册 Controller。

手敲顺序：先建目录 `src/main/java/com/example/shortlink/controller`，再建 `HealthController.java`。先只让类能编译，然后添加 Controller 注解，最后添加路由方法和 `record`。每写一步就让 IDE 的错误提示告诉你还缺什么 import 或语法。

约束：用 `record HealthResponse(String status)` 表示响应，Controller 不要直接返回 `Map`。

验收：启动后运行：

```bash
curl http://localhost:8080/api/health
```

思考：为什么访问普通类的方法不会自动成为 HTTP 接口？删除 `@RestController` 后响应有什么变化？

## 第 2 章：设计创建短链的 API

目标：实现 `POST /api/links`，接收原始链接和可选短码，并返回创建结果。

先写契约：

```json
{
  "originalUrl": "https://example.com/posts/hello",
  "code": "hello"
}
```

响应至少包含 `code`、`originalUrl`、`shortUrl`、`createdAt`。成功时返回 201。

你会学到：`record` DTO、`@RequestBody`、构造器注入、HTTP 状态码，以及 Java `Instant`。

建议目录：`dto/CreateShortLinkRequest.java`、`dto/ShortLinkResponse.java`、`controller/ShortLinkController.java`。先让 Controller 暂时返回固定数据，不要急着写存储。

验收：

```bash
curl -i -X POST http://localhost:8080/api/links \
  -H 'Content-Type: application/json' \
  -d '{"originalUrl":"https://example.com/posts/hello","code":"hello"}'
```

## 第 3 章：把业务从 Controller 拆出去

目标：创建 `service/ShortLinkService`。Controller 只处理 HTTP，Service 完成“生成或使用短码、记录创建时间、创建短链”。

你会学到：接口职责、`@Service`、依赖注入、`private final` 字段，以及为什么 Java 项目通常不把业务写在 Controller。

约束：Controller 不应直接出现 `Instant.now()`；它应调用 Service 并映射响应。

提示：写出 `ShortLink` 领域模型。可以选择不可变 `record ShortLink(...)`。先思考领域模型和 API DTO 是否一定相同，答案通常是否。

## 第 4 章：实现内存存储

目标：定义 `repository/ShortLinkRepository` 接口，提供 `findByCode` 与 `save`；再实现 `InMemoryShortLinkRepository`。

你会学到：接口、多态、`Optional`、`ConcurrentHashMap`，以及依赖倒置。它相当于前端用一个数据访问模块隔开页面逻辑和 `fetch`。

约束：Service 依赖 Repository 接口，不依赖内存实现类。缺失记录不能返回 `null`。

验收：同一短码创建两次时应能检测到；服务重启后数据消失是本章预期行为。

## 第 5 章：短码与重定向

目标：实现自动短码生成，以及 `GET /{code}` 返回 `302 Found` 并设置 `Location` 到原始 URL。

你会学到：字符表、数字进制转换、`AtomicLong`、并发的基本概念和 `ResponseEntity`。

建议：使用包含大小写字母和数字的 62 个字符表。先实现从递增数字转换成短码，再考虑随机码的碰撞问题。

验收：先创建链接，然后执行：

```bash
curl -I http://localhost:8080/hello
```

应看到 `302` 和 `Location: https://example.com/posts/hello`。不要用 301：浏览器会缓存它，开发阶段很难调试。

## 第 6 章：校验与统一错误

目标：为原始 URL、短码增加 Bean Validation；不存在时返回 404，短码冲突返回 409，输入不合法返回 400。

你会学到：`@Valid`、`@NotBlank`、`@Pattern`、异常、`@RestControllerAdvice` 与 Problem Details。

约束：不允许 Controller 中到处写 `try/catch`。定义表达业务含义的异常，例如 `LinkNotFoundException`。

验收：分别传入空 URL、非法短码、重复短码和不存在短码。检查每种响应都有正确 HTTP 状态码，而非只看 JSON 文本。

## 第 7 章：为 Service 写单元测试

目标：测试创建自定义短码、自动生成短码、重复短码报错、找不到短码报错。测试放在 `src/test/java` 下，并遵循主代码包结构。

你会学到：JUnit 5、Arrange-Act-Assert、断言、测试命名，以及为什么要给时间和随机性留出可替换的边界。

验收：

```bash
./mvnw test
```

提示：Service 如果直接调用 `Instant.now()`，断言时间会变得脆弱。尝试注入 `Clock`，测试用固定时间。

## 第 8 章：从内存迁移到数据库

目标：添加 Spring Data JPA 和 PostgreSQL 驱动，把内存 Repository 替换为数据库实现；为 `code` 加唯一索引。

你会学到：Entity、主键、事务、迁移、环境配置和连接池。不要把数据库密码提交进仓库，使用环境变量或本地未提交的配置文件。

验收：重启服务后仍能跳转已创建的短链；并发创建同一短码时，数据库唯一索引是最终防线。

## 第 9 章：并发不是“加 synchronized”

目标：理解为什么“先查短码是否存在，再保存”在多个请求同时到达时不可靠。先为 Repository 设计一个原子写入契约，例如 `saveIfAbsent`：内存实现用 `putIfAbsent`，PostgreSQL 依靠唯一约束兜底。

练习：编写 64 个并发请求同一短码的测试。只有一个请求应成功，其余得到冲突。`AtomicLong` 只解决单实例内生成器的竞争；多实例必须依靠共享 ID 源与数据库唯一约束。

## 第 10 章：可观测性不是打印日志

目标：引入 Actuator，区分“进程活着”“能接流量”“依赖是否正常”。

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
curl http://localhost:8080/actuator/metrics
```

思考：数据库短暂故障时，为什么不应让所有实例的 liveness 同时失败并被重启？哪些指标真正代表用户体验？

## 第 11 章：缓存、超时与降级

目标：为读取短链设计 cache-aside 接口，并用 TTL 内存实现先验证语义，再替换成 Redis。

约束：缓存未命中可回源数据库；缓存不可用也可有限回源；数据库不可用时快速 503；不能无限重试或无限排队。分别为缓存穿透、击穿、雪崩写一条测试或故障演练。

## 第 12 章：从应用到系统设计

阅读 [ARCHITECTURE-LAB.zh-CN.md](ARCHITECTURE-LAB.zh-CN.md)。它涵盖容量目标、读写分离、限流隔离、消息幂等、监控告警、测试、容灾、安全与交付。按其中“建议的学习顺序”逐步实现，每一阶段先定义 SLO 与失败行为，再选择组件。

## 第 13 章：战争模拟与事故复盘

阅读并执行 [WAR-GAME-LAB.zh-CN.md](WAR-GAME-LAB.zh-CN.md)。从建立容量基线开始，逐步模拟并发冲突、Redis 故障、慢依赖、连接池耗尽、网络丢包、磁盘水位和数据库恢复。每次实验都必须有停止条件、恢复步骤、指标记录与复盘结论。

## 推荐节奏

每次只完成一章：先读目标，合上教程手敲，运行验收命令，再阅读错误信息。遇到编译错误时，优先读第一条错误及其文件行号；这会比直接搜索完整答案更能建立 Java 的工作方式。
