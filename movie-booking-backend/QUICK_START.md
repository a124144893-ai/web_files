# 快速启动指南

## 五分钟快速启动

### 第一步：环境准备
```bash
# 检查Java版本 (需要17+)
java -version

# 检查Maven版本 (需要3.8+)
mvn -version

# 启动MySQL (Windows)
mysql -u root -p

# 创建数据库
CREATE DATABASE movie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 第二步：克隆和配置
```bash
# 进入项目目录
cd movie-booking-backend

# 编辑配置文件 (修改数据库密码)
# Linux/Mac:
vi src/main/resources/application.yml
# Windows:
notepad src\main\resources\application.yml
```

### 第三步：启动应用
```bash
# 方式一：使用Maven运行
mvn clean spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/movie-booking-app-1.0.0.jar
```

### 第四步：验证应用
```
✅ 应用启动成功，访问：
- Swagger UI:  http://localhost:8080/api/swagger-ui.html
- API文档:     http://localhost:8080/api/v3/api-docs
```

---

## 常用命令速查

| 操作 | 命令 |
|------|------|
| 清理项目 | `mvn clean` |
| 编译代码 | `mvn compile` |
| 运行测试 | `mvn test` |
| 打包项目 | `mvn package` |
| 跳过测试打包 | `mvn package -DskipTests` |
| 启动应用 | `mvn spring-boot:run` |
| 生成IDEA配置 | `mvn idea:idea` |
| 查看依赖树 | `mvn dependency:tree` |
| 检查更新版本 | `mvn versions:display-dependency-updates` |

---

## API测试 (使用curl)

### 1. 用户注册
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123",
    "phone": "13800138000"
  }'
```

### 2. 用户登录
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. 获取热门电影
```bash
curl http://localhost:8080/api/movies/hot
```

### 4. 获取电影详情
```bash
curl http://localhost:8080/api/movies/1
```

### 5. 搜索电影
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "漫威",
    "pageNum": 1,
    "pageSize": 10
  }'
```

### 6. 获取用户个人信息 (需要Token)
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <your_token_here>"
```

### 7. 创建预约 (需要Token)
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token_here>" \
  -d '{
    "showtimeId": 1,
    "seatCount": 2,
    "seatNumbers": "A1,A2"
  }'
```

---

## 常见问题排查

### 问题1：连接MySQL失败
```
Error: com.mysql.cj.jdbc.exceptions.CommunicationsException
```

**解决方案：**
1. 检查MySQL服务是否运行
2. 验证用户名和密码
3. 检查application.yml中的连接字符串

### 问题2：端口被占用
```
Error: Application failed to start with port 8080
```

**解决方案：**
```bash
# Windows: 查看占用8080端口的进程
netstat -ano | findstr :8080

# macOS/Linux: 查看占用8080端口的进程
lsof -i :8080

# 改用其他端口
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

### 问题3：JWT Token失效
```
Error: Token has expired
```

**解决方案：**
1. 重新登录获取新Token
2. 检查系统时间是否正确
3. 查看application.yml中的jwt.expiration配置

---

## 数据库初始化脚本

### 创建完整的示例数据

```sql
-- 插入示例电影数据
INSERT INTO movies (title, description, poster_url, director, cast, duration_minutes, rating, release_date, is_hot, created_at, updated_at) 
VALUES 
('漫威英雄：无限战争', '复仇者联盟第三部', 'https://example.com/mv1.jpg', '罗素兄弟', '小罗伯特·唐尼', 169, 8.5, '2024-04-20', TRUE, NOW(), NOW()),
('流浪地球2', '中国科幻大作', 'https://example.com/mv2.jpg', '张艺谋', '吴京', 173, 8.2, '2024-02-10', TRUE, NOW(), NOW()),
('奥本海默', '传记剧情片', 'https://example.com/mv3.jpg', '克里斯托弗·诺兰', '基里安·墨菲', 180, 8.8, '2024-01-15', TRUE, NOW(), NOW());

-- 插入示例放映场次
INSERT INTO showtimes (movie_id, start_time, available_seats, total_seats, cinema_hall, price, created_at, updated_at)
VALUES 
(1, '2024-04-21 14:00:00', 50, 100, '1厅IMAX', 68.0, NOW(), NOW()),
(1, '2024-04-21 19:00:00', 30, 100, '1厅IMAX', 68.0, NOW(), NOW()),
(1, '2024-04-22 10:00:00', 80, 100, '2厅', 39.0, NOW(), NOW()),
(2, '2024-04-21 15:30:00', 45, 80, '3厅', 45.0, NOW(), NOW()),
(3, '2024-04-21 20:00:00', 60, 100, '4厅', 58.0, NOW(), NOW());

-- 插入示例用户
INSERT INTO users (username, password, email, phone, role, created_at, updated_at)
VALUES 
('admin', '$2a$10$..encrypted_password..', 'admin@example.com', '13800138000', 'ADMIN', NOW(), NOW()),
('user1', '$2a$10$..encrypted_password..', 'user1@example.com', '13800138001', 'USER', NOW(), NOW());
```

---

## 开发工具推荐

### IDE
- **IntelliJ IDEA** (推荐) - 专业版有Spring Boot专有支持
- Visual Studio Code - 轻量级，需要配置插件
- Eclipse - 开源免费

### 数据库工具
- **DBeaver** (推荐) - 功能强大，支持多数据库
- Navicat - 商业软件，易用性好
- MySQL Workbench - 轻量级，官方工具

### API测试工具
- **Postman** (推荐) - 功能完整，有协作功能
- Insomnia - 开源轻量
- VS Code REST Client插件 - 轻量级

### 版本控制
- **Git** - 必须掌握
  ```bash
  git log --oneline        # 查看历史
  git branch -a            # 查看分支
  git merge <branch>       # 合并分支
  git rebase <branch>      # 变基
  ```

---

## 性能测试

使用Apache Bench进行性能测试：

```bash
# 安装 (macOS)
brew install httpd

# 测试并发性能
ab -n 1000 -c 100 http://localhost:8080/api/movies/hot

# 报告说明：
# Requests per second: 吞吐量（高越好）
# Time per request: 平均响应时间（低越好）
# Failed requests: 失败请求数（应为0）
```

使用JMeter进行复杂场景测试：

```bash
# 下载: https://jmeter.apache.org/
# 使用GUI创建测试计划，模拟真实业务场景
```

---

## 持续集成 (CI/CD) 配置示例

### GitHub Actions (.github/workflows/build.yml)

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: password
          MYSQL_DATABASE: movie_db
        options: >-
          --health-cmd="mysqladmin ping"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=3
        ports:
          - 3306:3306

    steps:
    - uses: actions/checkout@v3
    - name: Set up Java
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'adopt'
    - name: Build
      run: mvn clean install
    - name: Test
      run: mvn test
```

---

## Troubleshooting工具清单

| 工具 | 用途 | 命令 |
|------|------|------|
| curl | HTTP请求测试 | `curl http://localhost:8080/api/movies` |
| jps | 查看Java进程 | `jps -l` |
| jstat | 查看JVM统计 | `jstat -gc <pid>` |
| jmap | 内存快照 | `jmap -dump:live,format=b,file=heap.bin <pid>` |
| jhat | 堆文件分析 | `jhat heap.bin` |
| arthas | 在线诊断 | `java -jar arthas-boot.jar` |

---

**更新时间**: 2024-04-06  
**版本**: 1.0.0
