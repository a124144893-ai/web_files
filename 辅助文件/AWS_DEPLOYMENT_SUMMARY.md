# 📋 AWS 部署总结 & 快速开始

## 你已收到的部署文档

| 文件名 | 用途 | 优先级 |
|--------|------|--------|
| **AWS_DEPLOYMENT_GUIDE.md** | 完整部署指南，详细解释每一步 | ⭐⭐⭐ |
| **AWS_QUICK_DEPLOY_CHECKLIST.md** | 快速检查表，一步步操作指南 | ⭐⭐⭐ |
| **deploy.sh** | 自动化部署脚本，在 EC2 上执行 | ⭐⭐ |
| **application-prod.yml** | 生产环境配置模板，替换占位符后使用 | ⭐⭐ |
| **AWS_CLI_REFERENCE.md** | AWS CLI 命令参考，高级用户 | ⭐ |

---

## ⚡ 最快的部署步骤（15分钟版）

### Step 1: AWS 控制台创建资源（10分钟）

**创建 EC2 实例：**
```
1. AWS 控制台 → EC2 → Instances → Launch Instance
2. AMI: Amazon Linux 2
3. Type: t3.small
4. Security Group: 开放 22, 80, 443, 8080
5. Key Pair: 创建并下载 .pem 文件
6. Launch 后记录：公有 IP 地址
```

**创建 RDS 数据库：**
```
1. AWS 控制台 → RDS → Databases → Create Database
2. Engine: MySQL 8.0
3. Template: Free tier
4. Master username: admin
5. Master password: 生成强密码（记住！）
6. 初始数据库名: movie_db
7. Create Database 后记录：RDS Endpoint
```

### Step 2: 连接并部署应用（5分钟）

```bash
# 1. 连接到 EC2
ssh -i your-key.pem ec2-user@your-ec2-ip

# 2. 运行部署脚本
curl -O https://your-bucket/deploy.sh
bash deploy.sh

# 3. 上传 JAR 文件（在本地执行）
scp -i your-key.pem movie-booking-app-1.0.0.jar ec2-user@your-ec2-ip:/opt/app/

# 4. 配置数据库连接（SSH 连接后）
sudo vim /opt/app/application-prod.yml
# 修改 RDS_ENDPOINT_HERE 和 RDS_PASSWORD_HERE

# 5. 启动应用
sudo systemctl start movie-booking

# 6. 测试
curl http://your-ec2-ip:8080/movies/hot
```

---

## 🎯 必须记住的信息

创建资源时务必记录以下信息：

```
EC2 信息：
├─ 公有 IP: ___________________
├─ 私有 IP: ___________________
├─ 实例 ID: ___________________
├─ 密钥对文件: your-key.pem
└─ SSH 命令: ssh -i your-key.pem ec2-user@IP

RDS 信息：
├─ 端点: ___________________
├─ 端口: 3306
├─ 用户名: admin
├─ 密码: ___________________
└─ 数据库: movie_db

应用信息：
├─ 访问 URL: http://EC2-IP:8080
├─ JAR 位置: /opt/app/movie-booking-app-1.0.0.jar
├─ 配置文件: /opt/app/application-prod.yml
└─ 日志: sudo journalctl -u movie-booking -f
```

---

## 🔧 部署过程中的常见操作

### 查看应用日志
```bash
# 实时日志
sudo journalctl -u movie-booking -f

# 最后 100 行
sudo journalctl -u movie-booking -n 100

# 保存到文件
sudo journalctl -u movie-booking > app.log
```

### 重启应用
```bash
sudo systemctl restart movie-booking
```

### 停止应用
```bash
sudo systemctl stop movie-booking
```

### 检查应用状态
```bash
sudo systemctl status movie-booking
```

### 查看哪个进程占用 8080 端口
```bash
sudo netstat -tulpn | grep 8080
# 或
sudo lsof -i :8080
```

### 从 EC2 连接 RDS 数据库
```bash
mysql -h your-rds-endpoint.amazonaws.com -u admin -p
# 输入密码后可以执行 SQL 命令

# 查看数据库
SHOW DATABASES;
USE movie_db;
SELECT * FROM movies;
```

---

## 🧪 测试验证

### 测试 1：检查应用是否运行
```bash
curl http://your-ec2-ip:8080/movies/hot
# 应该返回电影列表 JSON
```

### 测试 2：测试登录
```bash
curl -X POST http://your-ec2-ip:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"lisi","password":"admin123"}'
# 应该返回 JWT token
```

### 测试 3：使用 Token 访问受保护接口
```bash
TOKEN="你的JWT令牌"
curl -H "Authorization: Bearer $TOKEN" \
  http://your-ec2-ip:8080/bookings/my
# 应该返回用户的预约列表
```

### 测试 4：检查数据库连接
```bash
# SSH 到 EC2
ssh -i your-key.pem ec2-user@your-ec2-ip

# 连接数据库
mysql -h your-rds-endpoint.amazonaws.com -u admin -p movie_db

# 查询数据
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM movies;
SELECT COUNT(*) FROM bookings;
```

---

## ❌ 问题排查

### 问题 1：无法 SSH 连接 EC2

**解决步骤：**
1. 检查 .pem 文件权限：`chmod 400 your-key.pem`
2. 检查安全组是否允许 22 端口
3. 检查 EC2 实例状态是否为 "running"
4. 检查用户名是否正确（Amazon Linux 2 用 ec2-user，Ubuntu 用 ubuntu）

### 问题 2：应用无法启动

```bash
# 查看详细错误
sudo journalctl -u movie-booking -n 100 -p err

# 检查 Java 是否正确安装
java -version

# 检查 JAR 文件是否存在
ls -lah /opt/app/movie-booking-app-1.0.0.jar
```

### 问题 3：无法连接 RDS 数据库

```bash
# 从 EC2 测试连接
mysql -h your-rds-endpoint -u admin -p

# 如果失败，检查：
# 1. RDS 安全组是否允许来自 EC2 安全组的 3306 访问
# 2. RDS 端点是否正确
# 3. 用户名和密码是否正确
```

### 问题 4：8080 端口无法访问

```bash
# 检查端口是否监听
sudo netstat -tulpn | grep 8080

# 检查应用是否运行
sudo systemctl status movie-booking

# 检查安全组是否允许 8080 入站
# AWS 控制台 → EC2 → Security Groups → 你的安全组
```

### 问题 5：数据库中没有数据

```bash
# 检查 data.sql 是否被执行
mysql -h your-rds-endpoint -u admin -p movie_db
SELECT * FROM movies;

# 如果为空，手动执行 SQL
mysql -h your-rds-endpoint -u admin -p movie_db < /opt/app/data.sql
```

---

## 📊 成本详情

### 免费套层成本（如果符合条件）

如果你使用的是 AWS 免费套层，以下资源完全免费（一年内）：
- EC2: t2.micro（每月 750 小时）
- RDS: db.t2.micro（每月 750 小时）+ 20GB 存储
- Data Transfer: 15GB/月（出站）

**月度成本：$0**（在免费套层限制内）

### 非免费套层成本

使用 t3.small + db.t3.micro：
- **EC2**: ~$15/月
- **RDS**: ~$15/月
- **Data Transfer**: ~$1/月
- **总计**: ~$31/月

---

## 🚀 下一步优化

部署完成后，你可以：

1. **配置域名（Route 53）**
   ```bash
   # 购买域名或转移现有域名到 Route 53
   # 创建 A 记录指向 EC2 IP
   ```

2. **启用 HTTPS（AWS Certificate Manager）**
   ```bash
   # 申请免费 SSL 证书
   # 配置 ALB 监听器 443
   ```

3. **使用 Application Load Balancer**
   ```bash
   # 分散流量，提高可用性
   # 自动转发失败流量
   ```

4. **启用 Auto Scaling**
   ```bash
   # 根据 CPU 使用率自动启动新实例
   ```

5. **配置备份和灾难恢复**
   ```bash
   # RDS 自动快照
   # 跨区域副本
   ```

---

## 📞 需要帮助？

### 常见问题快速查找

- **连接问题** → AWS_QUICK_DEPLOY_CHECKLIST.md 第 5 部分
- **部署步骤** → AWS_QUICK_DEPLOY_CHECKLIST.md 第 2 部分
- **命令参考** → AWS_CLI_REFERENCE.md
- **完整指南** → AWS_DEPLOYMENT_GUIDE.md
- **配置文件** → application-prod.yml（含详细注释）

---

## ✅ 部署完成检查

当所有以下条件都满足时，部署成功：

- [ ] **EC2 实例正在运行**
- [ ] **RDS 数据库可访问**
- [ ] **SSH 连接成功**
- [ ] **Java 21 已安装**
- [ ] **应用 JAR 已上传**
- [ ] **应用已启动** (`sudo systemctl status movie-booking` 显示 active/running)
- [ ] **数据库包含数据** (SELECT * FROM movies; 返回结果)
- [ ] **API 可访问** (curl 测试返回 200)
- [ ] **日志中无错误** (sudo journalctl 查看)

---

**🎉 祝贺！你的电影订票系统已在 AWS 上线运行！**

---

最后更新：2026 年 4 月 6 日
