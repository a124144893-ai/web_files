# 电影预约APP - 架构设计文档

## 系统架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                       客户端 (Client Layer)                   │
│                                                               │
│    Mobile app / Web browser                                  │
└────────────────────┬────────────────────────────────────────┘
                     │ REST API / HTTPS
┌────────────────────▼────────────────────────────────────────┐
│                  API 网关 / 负载均衡                           │
│                                                               │
│           Nginx / AWS ALB (可选)                              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│              Controller Layer (表示层)                       │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │MovieController│  │SearchController│  │BookingController│    │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│     ↓ POST/GET        ↓ POST         ↓ POST/DELETE          │
│
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│            Service Layer (业务逻辑层)                        │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ MovieService  SearchService  BookingService UserService│  │
│  │ (业务逻辑、数据验证、事务管理)                        │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│          Repository Layer (数据访问层)                       │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │MovieRepository│  │ShowtimeRepository│  │BookingRepository│    │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
└────────────────────┬────────────────────────────────────────┘
                     │ SQL / JDBC
┌────────────────────▼────────────────────────────────────────┐
│                数据持久化层 (Database Layer)                 │
│                                                               │
│        MySQL 8.0 (Primary)                                   │
│       /              \                                        │
│    主库              从库 (可选主从复制)                      │
│                                                               │
└────────────────────────────────────────────────────────────┘
```

## 分层架构详解

### 1. Controller 层（表示层）

**职责：**
- 处理HTTP请求和响应
- 请求参数验证
- 响应数据序列化
- 错误异常处理

**核心类：**
```
com.moviebooking.controller/
├── MovieController.java          # 电影相关接口
├── SearchController.java         # 搜索接口
├── BookingController.java        # 预约接口
└── UserController.java           # 用户接口
```

**设计模式：**
- REST风格API设计
- 统一的请求/响应格式
- 全局异常处理

### 2. Service 层（业务逻辑层）

**职责：**
- 实现业务逻辑
- 数据验证和转换
- 事务管理
- 跨domain业务流程

**核心类：**
```
com.moviebooking.service/
├── MovieService.java             # 接口定义
├── impl/
│   └── MovieServiceImpl.java      # 实现类
└── 其他Service...
```

**设计模式：**
- 接口与实现分离
- `@Transactional` 保证数据一致性
- 业务异常统一处理

### 3. Repository 层（数据访问层）

**职责：**
- 数据库CRUD操作
- Query构造和执行
- 数据库事务控制

**核心类：**
```
com.moviebooking.repository/
├── MovieRepository.java
├── ShowtimeRepository.java
├── BookingRepository.java
└── UserRepository.java
```

**技术栈：**
- Spring Data JPA
- Hibernate ORM
- JPQL查询

### 4. Entity 层（域模型）

**职责：**
- 定义数据库表结构
- 定义实体关系
- 数据验证注解

**核心类：**
```
com.moviebooking.entity/
├── User.java                    # 用户实体
├── Movie.java                   # 电影实体
├── Showtime.java               # 放映场次实体
├── Booking.java                # 预约实体
├── Role.java                   # 角色枚举
└── BookingStatus.java          # 预约状态枚举
```

### 5. DTO 层（数据传输对象）

**职责：**
- 服务与客户端之间的数据传输
- 数据格式转换
- 字段级别的数据验证

**核心类：**
```
com.moviebooking.dto/
├── request/                     # 请求DTO
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── BookingRequest.java
└── response/                    # 响应DTO
    ├── MovieResponse.java
    ├── BookingResponse.java
    └── LoginResponse.java
```

## 核心业务流程

### 用户登录流程

```
┌─────────────┐
│  客户端      │
└──────┬──────┘
       │ 1. POST /users/login
       │    (username, password)
       ▼
┌──────────────────────┐
│ UserController       │
└──────┬───────────────┘
       │ 2. login(request)
       ▼
┌──────────────────────┐
│ UserServiceImpl       │
│                      │
│ 1. 验证凭证          │
│ 2. 生成JWT Token     │
│ 3. 返回响应          │
└──────┬───────────────┘
       │ 3. LoginResponse
       │ (token, user info)
       ▼
┌──────────────────────┐
│  客户端               │
│  存储Token           │
└──────────────────────┘
```

### 电影预约流程

```
┌─────────────────┐
│  客户端           │
└────────┬────────┘
         │ 1. 选择:
         │    - 电影
         │    - 场次
         │    - 座位
         ▼
┌─────────────────────────────┐
│BookingController             │
│ 认证检查 (Token验证)          │
└────────┬────────────────────┘
         │ 2. createBooking(request)
         ▼
┌─────────────────────────────┐
│BookingServiceImpl             │
│                              │
│ 1. 查询放映场次              │
│ 2. 检查座位可用性            │
│ 3. 检查用户余额 (可选)       │
│ 4. 创建预约记录              │
│ 5. 更新可用座位数            │
│ 6. 记录审计日志              │
└────────┬────────────────────┘
         │
     ┌───┴─────────────────────────┐
     ▼                             ▼
┌──────────────────┐      ┌──────────────────┐
│Repository        │      │ 事务处理          │
│保存预约          │      │ 保证原子性         │
└──────┬───────────┘      └──────────────────┘
       │
       ▼
┌──────────────────┐
│ MySQL数据库      │
│ 保存预约记录     │
└──────┬───────────┘
       │ 3. BookingResponse
       │    (booking info)
       ▼
┌──────────────────┐
│  客户端           │
│  显示预约确认     │
└──────────────────┘
```

## 数据库关系图

```
┌──────────────┐
│    User      │
│              │
│ id (PK)      │
│ username     │
│ email        │
│ password     │
│ phone        │
│ role         │
└──────┬───────┘
       │ 1:N
       │
       │ (user_id)
       ▼
┌──────────────────┐
│   Booking        │
│                  │
│ id (PK)          │
│ user_id (FK)     │
│ showtime_id (FK) │
│ seat_count       │
│ total_price      │
│ status           │
└──────┬───────────┘
       │ N:1
       │
       │ (showtime_id)
       ▼
┌──────────────────┐
│   Showtime       │
│                  │
│ id (PK)          │
│ movie_id (FK)    │
│ start_time       │
│ available_seats  │
│ price            │
└──────┬───────────┘
       │ N:1
       │
       │ (movie_id)
       ▼
┌──────────────────┐
│    Movie         │
│                  │
│ id (PK)          │
│ title            │
│ description      │
│ director         │
│ rating           │
│ release_date     │
└──────────────────┘
```

## 安全架构

### 认证(Authentication)和授权(Authorization)

```
┌──────────────────────────────────────────────────────┐
│                HTTP Request                          │
│                 (with token)                         │
└────────────────┬─────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────┐
│        JwtAuthenticationFilter                       │
│                                                      │
│ 1. 从请求头提取Token                                │
│ 2. 验证Token签名和有效期                            │
│ 3. 提取用户ID                                        │
└────────────────┬─────────────────────────────────────┘
                 │ 有效
                 ▼
┌──────────────────────────────────────────────────────┐
│    CustomUserDetailsService                         │
│                                                      │
│ 1. 根据用户ID查询用户                               │
│ 2. 加载用户权限                                      │
│ 3. 创建UserDetails对象                              │
└────────────────┬─────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────┐
│    SecurityContext                                  │
│                                                      │
│ 设置认证信息                                         │
│ 供后续使用                                           │
└────────────────┬─────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────┐
│    @PreAuthorize 注解                               │
│                                                      │
│ 检查用户权限                                         │
│ 决定是否允许访问资源                                 │
└──────────────────────────────────────────────────────┘
```

### JWT Token结构

```
Header.Payload.Signature

Header:
{
  "alg": "HS512",
  "typ": "JWT"
}

Payload:
{
  "sub": "1",           // 用户ID
  "username": "user123",
  "iat": 1680000000,    // 签发时间
  "exp": 1680086400     // 过期时间 (24小时后)
}

Signature:
HMACSHA512(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret_key
)
```

## 数据流架构

### 查询流程

```
User Request
    │
    ▼
┌─────────────┐
│ Controller  │ ◄─── @GetMapping 路由
└──────┬──────┘
       │ Service.getXXX()
       ▼
┌─────────────┐
│  Service    │ ◄─── 业务逻辑处理
└──────┬──────┘
       │ Repository.findXXX()
       ▼
┌─────────────┐
│ Repository  │ ◄─── JPQL查询生成
└──────┬──────┘
       │ SQL查询
       ▼
┌─────────────┐
│  Database   │ ◄─── 返回结果集
└──────┬──────┘
       │ ResultSet
       ▼
┌─────────────┐
│ Entity映射  │ ◄─── Hibernate ORM
└──────┬──────┘
       │ DTO转换
       ▼
┌─────────────┐
│ JSON序列化  │ ◄─── Jackson
└──────┬──────┘
       │
       ▼
   Response
```

### 创建流程

```
User Request (POST with data)
    │
    ▼
┌─────────────────────┐
│ Request Validation  │ ◄─── @Valid, @NotNull等
└──────┬──────────────┘
       │ 有效
       ▼
┌─────────────────────┐
│  Service Logic      │ ◄─── Service处理业务规则
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│ @Transactional      │ ◄─── 事务开始
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│ Repository.save()   │ ◄─── INSERT SQL生成
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│ Database Persist    │ ◄─── 持久化到数据库
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│ @TransactionalCommit│ ◄─── 事务提交
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│ DTO Response        │ ◄─── 返回创建后的数据
└──────┬──────────────┘
       │
       ▼
   201 Created
```

## 并发控制机制

### 通过数据库约束

```sql
-- 表级约束
CREATE TABLE showtimes (
  id BIGINT PRIMARY KEY,
  total_seats INT NOT NULL,
  available_seats INT NOT NULL,
  -- 检查约束
  CONSTRAINT check_seats CHECK (available_seats >= 0 AND available_seats <= total_seats)
);
```

### 通过应用逻辑

```java
// 预约前检查
Integer bookedSeats = bookingRepository.findTotalBookedSeats(showtimeId);
if (bookedSeats + requestSeats > totalSeats) {
    throw new BusinessException("座位不足");
}

// 创建预约
Booking booking = new Booking(...);
bookingRepository.save(booking);

// 更新可用座位（原子操作）
showtime.setAvailableSeats(showtime.getAvailableSeats() - seatCount);
showtimeRepository.save(showtime);
```

### 通过乐观锁（可选改进）

```java
@Entity
public class Showtime {
    @Version
    private Long version;  // 乐观锁字段
    
    private Integer availableSeats;
}
```

## 缓存架构（可选）

```
┌────────────────┐
│  请求到达      │
└────────┬───────┘
         │
         ▼
    ┌─────────────┐
    │ Redis缓存？  │
    └──┬──────────┘
       │ Hit
       ▼
   返回缓存数据
   
       │ Miss
       ▼
   查询数据库
   
       │
       ▼
   写入缓存
   TTL: 30分钟
   
       │
       ▼
   返回数据
```

### 缓存策略

**可缓存数据：**
- 热门电影列表 (TTL: 1小时)
- 电影详情页面 (TTL: 30分钟)
- 用户信息 (TTL: 15分钟)
- 电影类型列表 (TTL: 1天)

**不缓存：**
- 预约信息 (实时性要求高)
- 座位可用性 (需要实时更新)
- 个人用户数据 (隐私敏感)

## 扩展性设计

### 横向扩展

```
┌─────────┐  ┌─────────┐  ┌─────────┐
│ App 1   │  │ App 2   │  │ App 3   │
└────┬────┘  └────┬────┘  └────┬────┘
     │            │            │
     └────────────┬────────────┘
                  │
                  ▼
           ┌────────────┐
           │Nginx/ALB   │ 负载均衡
           │Round Robin │
           └────────────┘
                  │
                  ▼
           ┌────────────┐
           │ MySQL DB   │ 共享数据库
           └────────────┘
```

### 垂直扩展

- 增加服务器CPU/内存
- 启用数据库连接池优化
- 实现查询缓存
- 数据库索引优化

### 功能模块解耦

```
预留接口：
- 支付服务microservice
- 推荐服务microservice
- 用户服务microservice
- 预约服务microservice

通过RabbitMQ/Kafka进行异步通信
```

## 监控和告警

```
┌─────────────────────────────┐
│  应用程序运行                 │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ Spring Boot Actuator        │ ◄─── 收集指标
│ /actuator/metrics           │
│ /actuator/health            │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ Prometheus (可选)            │ ◄─── 存储时间序列数据
│ 定期拉取指标                  │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ Grafana (可选)              │ ◄─── 可视化
│ 实时仪表板                    │
│ 性能监控                      │
└─────────────────────────────┘
```

---

**系统架构文档版本**: 1.0.0  
**最后更新**: 2024-04-06
