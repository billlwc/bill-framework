# Bill-Framework

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-red.svg)](https://maven.apache.org/)

> 一个基于 Spring Boot 3.3 + Java 21 的现代化企业级应用脚手架，开箱即用，助力快速开发。

## 📚 目录

- [特性](#-特性)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [核心功能](#-核心功能)
- [配置说明](#-配置说明)
- [使用示例](#-使用示例)
- [发布到 GitHub Packages](#-发布到-github-packages)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)

## ✨ 特性

- 🚀 **现代技术栈**：基于 Java 21 虚拟线程，Spring Boot 3.3.0
- 🎨 **模块化设计**：清晰的模块划分，便于扩展和维护
- 🌐 **国际化支持**：支持中文（简繁体）、英语、日语、韩语
- 🔐 **认证授权**：集成 Sa-Token，支持灵活的权限控制
- 📝 **接口文档**：集成 Knife4j，自动生成 API 文档
- 🔄 **统一响应**：标准化的响应格式和异常处理
- 📊 **日志追踪**：支持 TraceId 全链路追踪，MDC 跨线程传递
- 🔒 **分布式锁**：基于 Redisson 的分布式锁实现
- 🚦 **接口限流**：支持多维度限流（接口/IP/用户），防止恶意刷量
- 🛡️ **幂等处理**：基于 Redis 防重复提交，保证接口幂等性
- 💾 **Redis 功能**：完整的 Redis 操作封装，支持消息队列和延迟任务
- ⚡ **异步支持**：虚拟线程 + 传统线程池双执行器
- 🎯 **API 版本控制**：支持优雅的 API 版本管理

## 🛠 技术栈

| 技术                 | 版本      | 说明                |
|--------------------|---------|-------------------|
| Java               | 21      | JDK 版本            |
| Spring Boot        | 3.3.0   | 基础框架              |
| MyBatis-Plus       | 3.5.12  | ORM 框架            |
| PostgreSQL         | 42.6.0  | 数据库驱动             |
| Redis              | -       | 缓存和消息队列           |
| Redisson           | 3.50.0  | 分布式锁              |
| Sa-Token           | 1.44.0  | 认证授权              |
| Knife4j            | 4.5.0   | API 文档            |
| Hutool             | 5.8.39  | Java 工具库          |
| Lombok             | -       | 代码简化              |

## 📁 项目结构

```
bill-framework/
├── framework-base/          # 基础模块
│   ├── enums/              # 枚举类（响应码等）
│   ├── exception/          # 异常定义
│   ├── message/            # 国际化消息服务
│   ├── reply/              # 统一响应体
│   └── thread/             # 线程工具
├── framework-web/           # Web 模块
│   ├── advice/             # 统一响应拦截
│   ├── annotation/         # 自定义注解
│   ├── bo/                 # 业务对象
│   ├── config/             # 配置类
│   ├── entity/             # 基础实体
│   ├── exception/          # 异常处理
│   ├── log/                # 日志功能
│   ├── util/               # 工具类
│   └── version/            # API 版本控制
├── framework-redis/         # Redis 模块
│   ├── cache/              # 缓存管理
│   ├── config/             # Redis 配置
│   ├── idempotent/         # 幂等性处理
│   ├── lock/               # 分布式锁
│   ├── limit/              # 分布式限流
│   └── message/            # 消息队列
└── framework-test/          # 测试模块（示例）
    ├── controller/         # 控制器示例
    ├── service/            # 服务示例
    ├── mapper/             # Mapper 示例
    └── entity/             # 实体示例
```

## 🚀 快速开始

### 前置要求

- JDK 21+
- Maven 3.8+
- PostgreSQL 14+
- Redis 6.0+

### 1. 克隆项目

```bash
git clone https://github.com/billlwc/bill-framework.git
cd bill-framework
```

### 2. 配置数据库

修改 `framework-test/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果有密码
```

### 3. 编译安装

```bash
# 安装到本地 Maven 仓库
mvn clean install
```

### 4. 运行测试模块

```bash
cd framework-test
mvn spring-boot:run
```

### 5. 访问接口文档

启动成功后访问：

- Swagger UI: http://localhost:8080/doc.html
- 默认账号密码: `root` / `4396`

## 💡 核心功能

### 1. 统一响应封装

所有接口自动封装为统一格式：

```json
{
  "code": "SUCCESS",
  "msg": "请求成功",
  "data": { ... },
  "traceId": "1234567890"
}
```

**使用方式**：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    // 返回值会自动封装成 Result
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    // 不需要封装时使用 @NoResult 注解
    @NoResult
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        // 直接返回文件流
    }
}
```

### 2. 全局异常处理

```java
// 业务异常
throw new BusinessException(SysResponseCode.NOT_FOUND);
throw new BusinessException("user_not_found");  // 使用国际化 key

// 自动处理返回对应的 HTTP 状态码和错误信息
```

### 3. 参数校验

使用 Jakarta Bean Validation 进行参数校验，支持基础校验、分组校验、嵌套校验和自定义校验注解。

**基础校验**：

```java
@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在 6-32 个字符之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Min(value = 18, message = "年龄必须大于等于 18 岁")
    @Max(value = 120, message = "年龄必须小于等于 120 岁")
    private Integer age;
}

// Controller 中使用 @Valid 触发校验
@PostMapping("/register")
public String register(@Valid @RequestBody UserRegisterDTO dto) {
    return "注册成功";
}
```

**分组校验**：

```java
@Data
public class UserUpdateDTO {

    // 定义校验分组
    public interface UpdateBasic {}
    public interface UpdatePassword {}

    @NotNull(message = "用户ID不能为空", groups = {UpdateBasic.class, UpdatePassword.class})
    private Long id;

    @NotBlank(message = "用户名不能为空", groups = UpdateBasic.class)
    private String username;

    @NotBlank(message = "新密码不能为空", groups = UpdatePassword.class)
    @Size(min = 6, max = 32, groups = UpdatePassword.class)
    private String newPassword;
}

// 使用 @Validated 指定校验分组
@PutMapping("/user/basic")
public String updateBasic(@Validated(UserUpdateDTO.UpdateBasic.class) @RequestBody UserUpdateDTO dto) {
    return "更新成功";
}
```

**嵌套对象校验**：

```java
@Data
public class OrderCreateDTO {

    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @NotEmpty(message = "订单商品不能为空")
    @Valid  // 嵌套校验
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @DecimalMin(value = "0.01", message = "价格必须大于0")
        private BigDecimal price;

        @Min(value = 1, message = "数量至少为1")
        private Integer quantity;
    }
}
```

**自定义校验注解**：

```java
// 1. 定义注解
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "手机号格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. 实现校验器
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PHONE_PATTERN.matcher(value).matches();
    }
}

// 3. 使用自定义注解
@Data
public class SmsCodeDTO {
    @Phone(message = "手机号格式不正确")
    private String phone;
}
```

**常用校验注解**：
- `@NotNull` - 不能为 null
- `@NotBlank` - 不能为空字符串（去除空格后）
- `@NotEmpty` - 不能为空（集合、数组、字符串）
- `@Size(min, max)` - 长度限制
- `@Min` / `@Max` - 数值范围
- `@DecimalMin` / `@DecimalMax` - 小数范围
- `@Email` - 邮箱格式
- `@Pattern(regexp)` - 正则表达式
- `@Valid` - 嵌套对象校验

### 4. 方法日志记录

```java
@Service
public class UserService {

    @MethodLog(value = "查询用户", message = "根据ID查询用户详情")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
```

日志会自动记录：
- 方法参数
- 返回值
- 执行耗时
- TraceId
- 用户ID（如果已登录）

### 5. 分布式锁

```java
@Service
public class OrderService {

    @RedisLock(value = "'order:' + #orderId", timeout = 10, msg = "订单正在处理中")
    public void processOrder(Long orderId) {
        // 业务逻辑，同一订单同时只能有一个线程执行
    }
}
```

支持的参数：
- `value`: 锁的 key（支持 SpEL 表达式）
- `timeout`: 锁超时时间
- `timeUnit`: 时间单位
- `block`: 是否阻塞等待
- `msg`: 获取锁失败时的提示信息

### 6. 幂等性处理

基于 Redis `SET NX` 原子操作，防止同一请求被重复执行。

**适用场景**：重复点击提交、网络超时重试、MQ 消息重复消费等。

```java
// 默认：5秒内相同请求只能执行一次
@Idempotent
@PostMapping("/order/submit")
public Order submit(@RequestBody OrderDTO dto) { ... }

// 自定义有效期和提示
@Idempotent(timeout = 30, msg = "支付处理中，请勿重复提交")
@PostMapping("/pay/{orderId}")
public void pay(@PathVariable Long orderId) { ... }

// 自定义 key（SpEL 表达式）
@Idempotent(key = "'pay:' + #orderId", timeout = 30)
public void pay(@PathVariable Long orderId) { ... }

// 成功后立即解锁（允许再次提交）
@Idempotent(timeout = 10, deleteOnSuccess = true)
public void submitFeedback(@RequestParam String content) { ... }
```

**key 生成规则**（未指定 key 时）：
- 已登录：`用户ID + 类名:方法名 + 参数MD5`
- 未登录：`客户端IP + 类名:方法名 + 参数MD5`

**注意**：方法执行异常时，key 自动释放，允许重新提交。

支持的参数：
- `timeout`: 幂等有效期，默认 5 秒
- `timeUnit`: 时间单位
- `key`: 自定义 key（SpEL 表达式）
- `msg`: 重复提交时的提示信息
- `deleteOnSuccess`: 成功后是否立即释放，默认 false

### 7. 接口限流

基于 Redisson 令牌桶算法的分布式限流，支持多种限流维度：

**全局接口限流**：

```java
@RestController
public class ApiController {

    // 接口每秒最多 10 次请求（所有用户共享）
    @RateLimit(value = 10)
    @GetMapping("/api/search")
    public List<Item> search(String keyword) {
        return itemService.search(keyword);
    }
}
```

**按 IP 限流**：

```java
// 每个 IP 每分钟最多 60 次请求
@RateLimit(value = 60, time = 1, timeUnit = TimeUnit.MINUTES, type = RateLimitType.IP)
@GetMapping("/api/public/data")
public Data getPublicData() {
    return dataService.getData();
}
```

**按用户限流**：

```java
// 每个用户每秒最多 5 次请求
@RateLimit(value = 5, type = RateLimitType.USER, msg = "操作过于频繁，请稍后再试")
@PostMapping("/api/order/create")
public Order createOrder(@RequestBody OrderDTO dto) {
    return orderService.create(dto);
}
```

**自定义 key（SpEL 表达式）**：

```java
// 每个手机号每天最多发送 10 条短信
@RateLimit(
    value = 10,
    time = 1,
    timeUnit = TimeUnit.DAYS,
    key = "'sms:' + #phone",
    msg = "验证码发送过于频繁，每天最多10次"
)
@PostMapping("/api/sms/send")
public void sendSms(@RequestParam String phone) {
    smsService.send(phone);
}
```

支持的参数：
- `value`: 时间窗口内允许的最大请求数
- `time`: 时间窗口大小
- `timeUnit`: 时间单位（秒/分钟/小时/天）
- `key`: 自定义 key（支持 SpEL 表达式）
- `type`: 限流类型（DEFAULT/IP/USER）
- `msg`: 超限提示信息

### 8. Redis 消息队列

**发送消息**：

```java
@Autowired
private RedisUtil redisUtil;

// 即时消息
redisUtil.publishMessage("order:topic", message);

// 队列消息（立即消费）
redisUtil.sendQueueMessage("order:queue", orderData);

// 延迟消息（30分钟后消费）
redisUtil.sendQueueMessage("order:timeout", orderData, 30, TimeUnit.MINUTES);
```

**消费消息**：

```java
@Component
public class OrderConsumer implements RedisMsgConsumer {

    @Override
    public void redisMessage(String message) {
        // 处理消息
        log.info("收到订单消息: {}", message);
    }

    @Override
    public String redisTopic() {
        return "order:queue";  // 监听的队列名称
    }

    @Override
    public boolean queue() {
        return true;  // true=队列模式, false=发布订阅模式
    }
}
```

### 9. 分布式订单号生成

```java
@Autowired
private RedisUtil redisUtil;

// 生成唯一订单号：yyyyMMddHHmmss + 5位递增序列
BigInteger orderNo = redisUtil.generateOrderNo("ORDER:SEQ");
// 示例：20260217142530_00001
```

### 10. API 版本控制

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    // v1 版本：GET /api/v1/user/info
    @ApiVersion(1)
    @GetMapping("/info")
    public UserVO getUserInfoV1() {
        return new UserVO();  // 返回基础信息
    }

    // v2 版本：GET /api/v2/user/info
    @ApiVersion(2)
    @GetMapping("/info")
    public UserDetailVO getUserInfoV2() {
        return new UserDetailVO();  // 返回详细信息
    }
}
```

### 11. 国际化

**定义消息**（`messages_zh_CN.properties`）：

```properties
user_not_found=用户不存在
user_created=用户创建成功
```

**使用**：

```java
@Autowired
private MessageSourceService messageService;

String message = messageService.getMessage("user_not_found", null);

// 或者在异常中使用
throw new BusinessException("user_not_found");
```

支持的语言：
- 中文简体（zh_CN）
- 中文繁体（zh_TW）
- 英语（en）
- 日语（ja）
- 韩语（ko）

### 12. 异步任务

```java
@Service
public class NotificationService {

    // 使用虚拟线程执行器（默认）
    @Async
    public void sendEmail(String to, String content) {
        // 发送邮件逻辑
    }

    // 使用传统线程池
    @Async("async")
    public void sendSms(String phone, String content) {
        // 发送短信逻辑
    }
}
```

**特性**：
- 自动传递 TraceId（MDC 上下文）
- 支持虚拟线程（Java 21 特性）
- 异常统一处理和日志记录

### 13. MyBatis-Plus 自动填充

继承 `BaseEntity` 即可自动填充字段：

```java
@Data
@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String email;
    // id, createTime, updateTime 会自动填充
}
```

自动填充规则：
- `id`: 使用 Snowflake 算法生成
- `createTime`: 插入时自动填充当前时间
- `updateTime`: 更新时自动填充当前时间

## ⚙️ 配置说明

### 1. Sa-Token 配置

`framework-web/src/main/resources/bill-web.yml`:

```yaml
sa-token:
  token-name: Authorization          # Token 名称
  timeout: -1                        # Token 永不过期
  active-timeout: 3600               # 活跃超时时间（1小时）
  is-concurrent: false               # 不允许并发登录
  is-share: true                     # 共用 token
  token-style: tik                   # Token 风格
```

### 2. Knife4j 配置

```yaml
knife4j:
  enable: true                       # 启用增强功能
  production: false                  # 非生产环境
  basic:
    enable: true                     # 启用登录认证
    username: root                   # 文档访问账号
    password: 4396                   # 文档访问密码
```

### 3. Redis 配置

在 `application-dev.yml` 中配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

### 4. 虚拟线程配置

```yaml
spring:
  threads:
    virtual:
      enabled: true                  # 启用虚拟线程
```

## 📖 使用示例

### 创建一个完整的 CRUD 接口

**1. 实体类**：

```java
@Data
@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String email;
    private String phone;
}
```

**2. Mapper**：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

**3. Service**：

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @MethodLog(value = "查询用户", message = "分页查询用户列表")
    public IPage<User> page(RequestPageBO pageBO) {
        Page<User> page = new Page<>(pageBO.getPage(), pageBO.getSize());
        return userMapper.selectPage(page, null);
    }

    @MethodLog(value = "创建用户", message = "创建新用户")
    @RedisLock(value = "'user:create:' + #user.username", msg = "用户名重复操作")
    public void create(User user) {
        userMapper.insert(user);
    }
}
```

**4. Controller**：

```java
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public IPage<User> page(RequestPageBO pageBO) {
        return userService.page(pageBO);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public void create(@RequestBody User user) {
        userService.create(user);
    }
}
```

返回格式会自动封装为：

```json
{
  "code": "SUCCESS",
  "msg": "请求成功",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1
  },
  "traceId": "1234567890"
}
```

## 📦 发布到 GitHub Packages

### 1. 配置 GitHub Token

在 `~/.m2/settings.xml` 中添加：

```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_TOKEN</password>
  </server>
</servers>
```

### 2. 发布

```bash
mvn clean deploy
```

### 3. 使用已发布的包

在其他项目的 `pom.xml` 中添加：

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/billlwc/bill-framework</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>bill.framework</groupId>
    <artifactId>framework-web</artifactId>
    <version>1.1</version>
  </dependency>
</dependencies>
```

## 🎯 最佳实践

### 1. 异常处理

```java
// ✅ 推荐：使用业务异常 + 国际化
throw new BusinessException("user_not_found");

// ✅ 推荐：使用枚举
throw new BusinessException(SysResponseCode.NOT_FOUND);

// ❌ 不推荐：直接抛出运行时异常
throw new RuntimeException("用户不存在");
```

### 2. 日志使用

```java
// ✅ 推荐：使用 @MethodLog 记录关键业务
@MethodLog(value = "订单支付", message = "处理订单支付")
public void pay(Long orderId) { ... }

// ✅ 推荐：使用 Lombok 的 @Slf4j
@Slf4j
@Service
public class UserService {
    public void test() {
        log.info("TraceId 会自动打印");
    }
}
```

### 3. Redis 使用

```java
// ✅ 推荐：使用封装好的 RedisUtil
redisUtil.set("key", value, 3600);

// ✅ 推荐：使用分布式锁
@RedisLock("'lock:' + #id")
public void process(Long id) { ... }

// ✅ 推荐：使用限流保护接口
@RateLimit(value = 10, type = RateLimitType.USER)
public void submitOrder(OrderDTO dto) { ... }
```

### 4. 认证授权

```java
// 登录接口使用 @NoToken
@NoToken
@PostMapping("/login")
public String login(@RequestBody LoginDTO dto) {
    // 登录逻辑
    StpUtil.login(userId);
    return StpUtil.getTokenValue();
}

// 需要登录的接口自动校验
@GetMapping("/info")
public User info() {
    Long userId = StpUtil.getLoginIdAsLong();
    return userService.getById(userId);
}
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📝 待完成功能

- [x] 参数校验示例和文档
- [x] 限流功能实现
- [ ] 数据脱敏功能
- [ ] XSS 防护
- [ ] 操作审计日志
- [ ] 链路追踪集成
- [ ] 单元测试完善
- [ ] Docker 容器化
- [ ] CI/CD 配置

## 📄 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🔗 相关链接

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Sa-Token 官方文档](https://sa-token.cc/)
- [Knife4j 官方文档](https://doc.xiaominfo.com/)
- [Redisson 官方文档](https://redisson.org/)

## 📧 联系方式

如有问题，欢迎提交 Issue 或联系维护者。

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！
