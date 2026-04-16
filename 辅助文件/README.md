# 电影预约APP后端服务 - 项目框架说明文档

## 目录

1. [项目概述](#项目概述)
2. [技术栈](#技术栈)
3. [项目结构](#项目结构)
4. [快速开始](#快速开始)
5. [核心功能模块](#核心功能模块)
6. [API文档](#api文档)
7. [数据库设计](#数据库设计)
8. [安全认证](#安全认证)
9. [配置说明](#配置说明)
10. [部署指南](#部署指南)
11. [常见问题](#常见问题)

---

## 项目概述

**电影预约APP**是一个基于Spring Boot的后端服务，提供电影预约相关的所有功能。系统主要包含以下四大核心模块：

- **首页模块**：展示热门电影和推荐内容
- **搜索模块**：支持按关键词、类型、评分等多维度搜索电影
- **电影详情与预约模块**：提供电影详细信息和座位预约功能
- **用户中心模块**：用户登录注册、个人信息管理、预约记录查看

---

## 技术栈

### 核心框架和库

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.1.5 | 核心框架 |
| Spring Security | 6.1.x | 安全认证框架 |
| Spring Data JPA | 3.1.x | ORM框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.0+ | 缓存数据库（可选） |
| JWT | 0.12.3 | 令牌认证 |
| Springdoc OpenAPI | 2.0.2 | API文档生成 |
| Lombok | 1.18.x | 代码简化 |
| Maven | 3.8+ | 项目构建工具 |

---

## 项目结构

```
movie-booking-backend/
├── src/main/
│   ├── java/com/moviebooking/
│   │   ├── config/                 # 配置类
│   │   │   ├── SecurityConfig.java         # Spring Security配置
│   │   │   ├── OpenApiConfig.java          # Swagger配置
│   │   │   └── WebConfig.java              # Web配置（CORS等）
│   │   │
│   │   ├── controller/             # 控制器（HTTP端点）
│   │   │   ├── MovieController.java        # 电影接口
│   │   │   ├── SearchController.java       # 搜索接口
│   │   │   ├── BookingController.java      # 预约接口
│   │   │   └── UserController.java         # 用户接口
│   │   │
│   │   ├── dto/                    # 数据传输对象
│   │   │   ├── request/                    # 请求DTO
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── SearchRequest.java
│   │   │   │   └── BookingRequest.java
│   │   │   └── response/                   # 响应DTO
│   │   │       ├── MovieResponse.java
│   │   │       ├── ShowtimeResponse.java
│   │   │       ├── BookingResponse.java
│   │   │       ├── UserResponse.java
│   │   │       ├── LoginResponse.java
│   │   │       └── PageResponse.java
│   │   │
│   │   ├── entity/                 # JPA实体类
│   │   │   ├── User.java                   # 用户
│   │   │   ├── Movie.java                  # 电影
│   │   │   ├── Showtime.java               # 放映场次
│   │   │   ├── Booking.java                # 预约记录
│   │   │   ├── Role.java                   # 角色枚举
│   │   │   └── BookingStatus.java          # 预约状态枚举
│   │   │
│   │   ├── repository/             # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   ├── MovieRepository.java
│   │   │   ├── ShowtimeRepository.java
│   │   │   └── BookingRepository.java
│   │   │
│   │   ├── service/                # 业务逻辑层
│   │   │   ├── MovieService.java           # 接口
│   │   │   ├── SearchService.java
│   │   │   ├── BookingService.java
│   │   │   ├── UserService.java
│   │   │   └── impl/                       # 实现类
│   │   │       ├── MovieServiceImpl.java
│   │   │       ├── SearchServiceImpl.java
│   │   │       ├── BookingServiceImpl.java
│   │   │       └── UserServiceImpl.java
│   │   │
│   │   ├── security/               # 安全相关
│   │   │   ├── JwtUtil.java               # JWT工具类
│   │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   ├── JwtAuthEntryPoint.java      # JWT入口点
│   │   │   └── CustomUserDetailsService.java # 用户详情服务
│   │   │
│   │   ├── exception/              # 异常处理
│   │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BusinessException.java
│   │   │   └── ErrorResponse.java
│   │   │
│   │   ├── util/                   # 工具类
│   │   │   └── DateUtil.java
│   │   │
│   │   └── MovieBookingApplication.java    # 应用入口
│   │
│   └── resources/
│       ├── application.yml                 # 应用配置
│       └── logback-spring.xml              # 日志配置
│
├── pom.xml                                 # Maven配置
└── README.md                               # 项目说明
```

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Git

### 本地开发步骤

#### 1. 克隆项目
```bash
git clone <repository-url>
cd movie-booking-backend
```

#### 2. 创建数据库
```sql
CREATE DATABASE movie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 修改配置文件
编辑 `src/main/resources/application.yml`，配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf-8
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: create-drop  # 首次运行用create-drop自动创建表
```

#### 4. 安装依赖
```bash
mvn clean install
```

#### 5. 运行应用
```bash
mvn spring-boot:run
```

或者打包后运行：
```bash
mvn clean package
java -jar target/movie-booking-app-1.0.0.jar
```

#### 6. 访问应用
- **API文档**: http://localhost:8080/api/swagger-ui.html
- **API规范**: http://localhost:8080/api/v3/api-docs

---

## 核心功能模块

### 1. 用户模块 (User Module)

#### 主要功能
- 用户注册和登录
- 个人信息管理
- Token生成和验证
- 用户角色管理（USER、ADMIN）

#### 主要API
```
POST   /users/register                 # 用户注册
POST   /users/login                    # 用户登录
GET    /users/profile                  # 获取个人信息
PUT    /users/profile                  # 更新个人信息
GET    /users/{id}                     # 获取用户公开信息
```

#### DTO说明

**LoginRequest**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**RegisterRequest**
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phone": "13800138000"
}
```

**LoginResponse**
```json
{
  "userId": 1,
  "username": "user123",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "role": "USER"
}
```

### 2. 电影模块 (Movie Module)

#### 主要功能
- 获取热门电影
- 获取电影详情
- 查看电影的所有放映场次
- 电影信息管理

#### 主要API
```
GET    /movies/hot                     # 获取热门电影
GET    /movies/{id}                    # 获取电影详情
GET    /movies                         # 分页获取所有电影
GET    /movies/{movieId}/showtimes     # 获取电影的放映场次
```

#### 提供的数据信息
- 电影标题、描述、海报URL
- 导演、演员、片长、评分
- 上映日期、热门标签
- 所有放映场次信息

### 3. 搜索模块 (Search Module)

#### 主要功能
- 按关键词搜索电影
- 按类型筛选
- 按评分筛选
- 多维度组合搜索
- 获取所有电影类型列表

#### 主要API
```
POST   /search                         # 复杂搜索
GET    /search/keyword                 # 关键词搜索
GET    /search/genres                  # 获取所有类型
```

#### SearchRequest示例
```json
{
  "keyword": "漫威",
  "genre": "科幻",
  "minRating": 7,
  "maxRating": 10,
  "pageNum": 1,
  "pageSize": 10,
  "sortBy": "releaseDate"
}
```

### 4. 预约模块 (Booking Module)

#### 主要功能
- 创建电影预约
- 查看预约详情
- 查看用户预约历史
- 取消预约
- 座位管理和可用性检查

#### 主要API
```
POST   /bookings                       # 创建预约
GET    /bookings/{id}                  # 获取预约详情
GET    /bookings/my                    # 获取当前用户预约
DELETE /bookings/{id}                  # 取消预约
GET    /bookings/showtime/{showtimeId} # 获取场次的预约
```

#### BookingRequest示例
```json
{
  "showtimeId": 1,
  "seatCount": 2,
  "seatNumbers": "A1,A2"
}
```

#### BookingResponse示例
```json
{
  "id": 1,
  "movieId": 1,
  "movieTitle": "漫威英雄：无限战争",
  "showtimeStart": "2024-04-15 19:00:00",
  "cinemaHall": "1厅",
  "seatCount": 2,
  "seatNumbers": "A1,A2",
  "totalPrice": 100.0,
  "status": "CONFIRMED",
  "bookingTime": "2024-04-06 10:30:00"
}
```

---

## API文档

### 访问方式

启动应用后，访问以下地址查看完整的API文档：

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

### 认证方式

需要认证的API使用Bearer Token认证，在请求头中添加：

```
Authorization: Bearer <token>
```

### 响应格式

#### 成功响应 (200 OK)
```json
{
  "id": 1,
  "title": "电影标题",
  "description": "电影描述",
  ...
}
```

#### 错误响应 (4xx/5xx)
```json
{
  "status": 400,
  "message": "业务描述信息",
  "code": "ERROR_CODE",
  "path": "/api/endpoint",
  "timestamp": "2024-04-06 10:30:00"
}
```

### 错误代码

| 错误代码 | HTTP状态 | 说明 |
|---------|---------|------|
| RESOURCE_NOT_FOUND | 404 | 资源不存在 |
| BUSINESS_ERROR | 400 | 业务逻辑错误 |
| VALIDATION_ERROR | 400 | 请求参数验证失败 |
| UNAUTHORIZED | 401 | 未授权 |
| ACCESS_DENIED | 403 | 无权限访问 |
| INTERNAL_SERVER_ERROR | 500 | 服务器内部错误 |

---

## 数据库设计

### 用户表 (users)

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20),
  avatar_url VARCHAR(500),
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);
```

### 电影表 (movies)

```sql
CREATE TABLE movies (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  description VARCHAR(1000),
  poster_url VARCHAR(500),
  director VARCHAR(100),
  cast VARCHAR(500),
  duration_minutes INT,
  rating DECIMAL(3,1),
  release_date DATE NOT NULL,
  is_hot BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);
```

### 电影类型表 (movie_genres)

```sql
CREATE TABLE movie_genres (
  movie_id BIGINT NOT NULL,
  genre VARCHAR(50) NOT NULL,
  PRIMARY KEY (movie_id, genre),
  FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);
```

### 放映场次表 (showtimes)

```sql
CREATE TABLE showtimes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  available_seats INT NOT NULL,
  total_seats INT NOT NULL,
  cinema_hall VARCHAR(50),
  price DECIMAL(10,2),
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);
```

### 预约表 (bookings)

```sql
CREATE TABLE bookings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  showtime_id BIGINT NOT NULL,
  seat_count INT NOT NULL,
  seat_numbers VARCHAR(500),
  total_price DECIMAL(10,2),
  booking_time DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE
);
```

### 关键索引

```sql
CREATE INDEX idx_movies_is_hot ON movies(is_hot);
CREATE INDEX idx_movies_release_date ON movies(release_date);
CREATE INDEX idx_showtimes_movie_id ON showtimes(movie_id);
CREATE INDEX idx_showtimes_start_time ON showtimes(start_time);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_showtime_id ON bookings(showtime_id);
CREATE INDEX idx_bookings_status ON bookings(status);
```

---

## 安全认证

### JWT认证机制

系统使用JWT (JSON Web Tokens) 进行身份认证，流程如下：

1. **用户登录** → 系统验证用户名密码
2. **生成TOKEN** → 获取有效期为24小时的JWT token
3. **添加到请求** → 后续请求在Authorization头中携带token
4. **提交验证** → 系统验证token的有效性

### 通信原理

```
┌─────────────┐                    ┌─────────────────┐
│   客户端     │                    │   服务器         │
└──────┬──────┘                    └────────┬────────┘
       │                                    │
       │──── POST /users/login ──────────>│
       │                                   │
       │<──── LoginResponse(token) ──────│
       │                                   │
       │──── GET /users/profile ────────>│
       │       Header: Authorization:     │
       │       Bearer {token}             │
       │                                   │
       │<──── UserResponse ──────────────│
```

### 配置说明

**application.yml** 中的JWT配置：

```yaml
jwt:
  secret: movie-booking-secret-key-change-in-production-environment-please-use-strong-key-at-least-256-bits
  expiration: 86400000  # 24小时，单位：毫秒
```

**生产环境建议：**
- 生成强密钥（至少256位）
- 使用环境变量或密钥管理服务
- 定期轮换密钥
- 启用HTTPS

### 权限控制

使用 `@PreAuthorize` 注解进行方法级权限控制：

```java
@PreAuthorize("isAuthenticated()")           // 需要认证
@PreAuthorize("hasRole('ADMIN')")            // 需要ADMIN角色
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // 多角色判断
```

---

## 配置说明

### application.yml 配置详解

#### 数据源配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20      # 最大连接数
      minimum-idle: 5            # 最小空闲连接
      connection-timeout: 30000  # 连接超时时间（毫秒）
```

#### JPA配置
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update           # DDL策略: create-drop, create, update, validate
    properties:
      hibernate:
        format_sql: true         # 格式化SQL
        show-sql: false          # 打印SQL（生产环境关闭）
        jdbc:
          batch_size: 20         # 批处理大小
```

#### Redis配置（可选）
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000
```

#### 服务器配置
```yaml
server:
  port: 8080                     # 服务端口
  servlet:
    context-path: /api          # 应用上下文路径
```

#### 日志配置
```yaml
logging:
  level:
    root: INFO                   # 根日志级别
    com.moviebooking: DEBUG      # 应用日志级别
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### 环境变量配置

为了安全性，建议使用环境变量替代硬编码的配置：

```bash
export DB_URL=jdbc:mysql://localhost:3306/movie_db
export DB_USERNAME=root
export DB_PASSWORD=password
export JWT_SECRET=your-strong-secret-key
export JWT_EXPIRATION=86400000
```

对应的配置文件使用：
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET:default-secret}
  expiration: ${JWT_EXPIRATION:86400000}
```

---

## 部署指南

### 本地打包

```bash
# 清理并打包
mvn clean package

# 跳过测试打包（加快速度）
mvn clean package -DskipTests

# 输出文件位置
# target/movie-booking-app-1.0.0.jar
```

### Docker部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/movie-booking-app-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

构建镜像：
```bash
docker build -t movie-booking:1.0.0 .
```

运行容器：
```bash
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://db-host:3306/movie_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  --name movie-booking \
  movie-booking:1.0.0
```

### AWS EC2部署

#### 基本步骤

1. **启动EC2实例**
   - 选择Ubuntu 22.04 LTS镜像
   - 选择至少t3.small实例类型
   - 配置安全组，开放端口8080

2. **安装依赖**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jre-headless mysql-client-core -y
   ```

3. **上传应用**
   ```bash
   scp -i key.pem target/movie-booking-app-1.0.0.jar ubuntu@<EC2-IP>:/home/ubuntu/
   ```

4. **运行应用**
   ```bash
   cd /home/ubuntu/
   nohup java -jar movie-booking-app-1.0.0.jar > app.log 2>&1 &
   ```

5. **使用Nginx作为反向代理**
   ```bash
   sudo apt install nginx -y
   ```

   配置 `/etc/nginx/sites-available/default`：
   ```nginx
   server {
       listen 80;
       server_name _;

       location /api {
           proxy_pass http://localhost:8080/api;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

6. **数据库配置**
   - 使用AWS RDS创建MySQL实例
   - 配置安全组允许EC2访问
   - 修改application.yml中的数据库连接字符串

### Docker Compose部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: movie_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://mysql:3306/movie_db
      DB_USERNAME: root
      DB_PASSWORD: password
      REDIS_HOST: redis
    depends_on:
      - mysql
      - redis

volumes:
  mysql_data:
```

启动：
```bash
docker-compose up -d
```

---

## 常见问题

### Q1: 启动时出现"数据库连接失败"错误？

**解决方案：**
1. 检查MySQL是否正常运行：`mysql -u root -p`
2. 确认数据库存在：`SHOW DATABASES;`
3. 检查 application.yml 中的连接字符串和凭证
4. 确保使用了正确的JDBC驱动版本

### Q2: JWT token过期怎么处理？

**解决方案：**
- 前端收到401错误后，重新调用登录接口获取新token
- 可以实现token刷新机制（需要后端增加额外接口）
- 增加token有效期（但这会降低安全性）

### Q3: 如何查看应用日志？

**本地运行：**
- 日志直接打印到控制台

**后台运行：**
```bash
# 查看应用日志
tail -f logs/application.log

# 查看最后100行
tail -100 logs/application.log

# 搜索特定错误
grep "ERROR" logs/application.log
```

### Q4: 如何修改端口号？

在 `application.yml` 中修改：
```yaml
server:
  port: 9090  # 改为需要的端口号
```

### Q5: 性能优化建议？

1. **启用Redis缓存**：缓存热门电影列表
2. **数据库优化**：添加适当的索引
3. **连接池调整**：根据并发量调整Hikari参数
4. **异步处理**：使用 `@Async` 处理耗时操作
5. **API限流**：使用 `@RateLimiter` 做请求限流

### Q6: 如何处理并发预约？

系统已使用以下机制保证并发安全：

1. **数据库约束**：座位总数限制
2. **事务管理**：`@Transactional` 保证操作原子性
3. **库存检查**：预约前检查可用座位
4. **悲观锁**：可使用 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 加强

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Showtime s WHERE s.id = :id")
Optional<Showtime> findByIdWithLock(@Param("id") Long id);
```

### Q7: 如何监控应用健康状态？

添加Spring Boot Actuator（可选）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

配置：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  endpoint:
    health:
      show-details: always
```

访问：http://localhost:8080/api/actuator/health

### Q8: 如何实现功能的灰度发布？

建议方案：
1. 使用特性开关（Feature Toggle）
2. 使用Spring Cloud Config管理配置
3. 使用健康检查和金丝雀部署

---

## 开发规范

### 代码规范

1. **类名**：使用PascalCase，如 `MovieController`
2. **方法名**：使用camelCase，如 `getMovieById`
3. **常量**：使用UPPER_CASE，如 `MAX_PAGE_SIZE`
4. **包名**：全小写，如 `com.moviebooking.controller`

### Git提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

Type类型：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档
- `style`: 代码风格
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建工具/依赖

示例：
```
feat(booking): 实现电影预约功能

- 添加预约创建接口
- 实现座位可用性检查
- 更新可用座位数
```

### 测试规范

- 单元测试覆盖率 > 80%
- 重要业务逻辑必须有集成测试
- 使用 `@SpringBootTest` 进行集成测试

---

## 后续改进建议

### 第一阶段
- [ ] 添加更详细的业务日志
- [ ] 实现token刷新机制
- [ ] 添加请求限流
- [ ] 完整的单元测试和集成测试

### 第二阶段
- [ ] Redis缓存集成
- [ ] Elasticsearch全文搜索
- [ ] 消息队列异步处理
- [ ] 数据库主从复制

### 第三阶段
- [ ] 支付集成（在线支付）
- [ ] 电子票券生成
- [ ] 实时座位显示
- [ ] 用户评分功能

### 第四阶段
- [ ] 微服务架构改造
- [ ] ServiceMesh集成
- [ ] K8s容器编排
- [ ] 完整的监控告警系统

---

## 联系与支持

如有任何问题或建议，欢迎通过以下方式联系：

- 📧 Email: support@moviebooking.com
- 🐞 Issues: [GitHub Issues]
- 📝 Documentation: [Wiki]

---

## 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。

---

**最后更新时间**: 2024-04-06  
**项目版本**: 1.0.0
