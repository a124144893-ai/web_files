# 📦 AWS 部署文件清单

## 你现在拥有的所有部署相关文件

```
C:\web_project\
├── movie-booking-backend/
│   ├── target/
│   │   └── movie-booking-app-1.0.0.jar         ⭐ 部署的应用文件
│   ├── src/main/resources/
│   │   └── data.sql                             ⭐ 数据库初始化脚本
│   └── application-prod.yml (复制到此处)        ⭐ 生产配置
│
├── AWS_DEPLOYMENT_GUIDE.md                      📖 完整部署指南（必读）
├── AWS_QUICK_DEPLOY_CHECKLIST.md                ✅ 快速检查清单（按照操作）
├── AWS_DEPLOYMENT_SUMMARY.md                    📋 总结和快速开始
├── AWS_CLI_REFERENCE.md                         💻 CLI 命令参考
├── deploy.sh                                    🔧 自动化部署脚本
├── application-prod.yml                         ⚙️  生产配置模板
│
└── INSERT_MOVIES.sql                           (可选) SQL 数据脚本
```

---

## 🎯 按优先级阅读顺序

### 第 1 优先级（必读）
1. **AWS_DEPLOYMENT_SUMMARY.md** - 理解整体流程（5分钟）
2. **AWS_QUICK_DEPLOY_CHECKLIST.md** - 按步骤操作（20-30分钟）

### 第 2 优先级（参考）
3. **AWS_DEPLOYMENT_GUIDE.md** - 深入理解细节（可选，30分钟）
4. **application-prod.yml** - 配置文件说明（修改配置时参考）

### 第 3 优先级（高级）
5. **AWS_CLI_REFERENCE.md** - 命令行工具（高级用户）
6. **deploy.sh** - 自动化脚本理解（高级用户）

---

## 📝 文件使用指南

### 1️⃣  application-prod.yml
**用途**: 在 EC2 上的生产环境配置文件

**使用步骤**：
```bash
# 在 EC2 上
sudo cp /opt/app/application-prod.yml /opt/app/application-prod.yml.backup

# 编辑配置
sudo vim /opt/app/application-prod.yml

# 修改以下内容：
# - RDS_ENDPOINT_HERE → 实际 RDS 端点
# - admin → RDS 用户名
# - RDS_PASSWORD_HERE → RDS 密码

# 重启应用生效
sudo systemctl restart movie-booking
```

### 2️⃣ deploy.sh
**用途**: 在 EC2 上一键安装 Java、创建目录结构等

**使用步骤**：
```bash
# 上传脚本到 EC2
scp -i key.pem deploy.sh ec2-user@ec2-ip:~/

# 远程执行
ssh -i key.pem ec2-user@ec2-ip
bash ~/deploy.sh

# 脚本会自动：
# ✓ 更新系统
# ✓ 安装 Java 21
# ✓ 创建应用目录
# ✓ 创建启动脚本
# ✓ 配置 systemd 服务
```

### 3️⃣ data.sql
**用途**: 数据库初始化脚本（用户、电影、预约等）

**使用步骤**：
```bash
# 上传到 EC2
scp -i key.pem data.sql ec2-user@ec2-ip:/opt/app/

# SSH 连接后执行
mysql -h rds-endpoint -u admin -p movie_db < /opt/app/data.sql

# 验证
mysql -h rds-endpoint -u admin -p movie_db
SELECT COUNT(*) FROM movies;
SELECT COUNT(*) FROM users;
```

### 4️⃣ movie-booking-app-1.0.0.jar
**用途**: Spring Boot 应用的可执行 JAR 包

**位置**: `C:\web_project\movie-booking-backend\target\`

**上传到 EC2**：
```bash
scp -i key.pem C:\web_project\movie-booking-backend\target\movie-booking-app-1.0.0.jar \
  ec2-user@ec2-ip:/opt/app/
```

**启动应用**：
```bash
# SSH 到 EC2 后
sudo systemctl start movie-booking

# 查看日志
sudo journalctl -u movie-booking -f
```

---

## 🔐 安全建议

在部署前检查：

- [ ] **AWS 密钥和密码** 
  - ✓ 不要提交到 Git/GitHub
  - ✓ 保存在安全的密码管理器
  - ✓ 定期轮换

- [ ] **配置文件中的敏感信息**
  - ✓ JWT Secret（修改为强密码）
  - ✓ RDS 密码（生成 20+ 字符强密码）
  - ✓ 生产环境关闭 show-sql 和 format-sql

- [ ] **网络安全**
  - ✓ 安全组只开放必要端口
  - ✓ RDS 不对互联网公开
  - ✓ SSH 密钥定期备份

- [ ] **应用安全**
  - ✓ 更改默认的 JWT Secret
  - ✓ 启用 HTTPS（生产环境必须）
  - ✓ 定期更新依赖（Maven/Spring Boot 升级）

---

## 💰 成本估算

### 使用免费套层（Amazon Linux 2 + MySQL）

```
EC2:           t2.micro  = 免费（750 小时/月）
RDS:           t2.micro  = 免费（750 小时/月）
存储:          20GB       = 免费
Data Transfer: 15GB/月    = 免费
────────────────────────────────
月度成本：                 $0/月
```

### 超过免费套层

```
EC2:           t3.small  = $15/月
RDS:           t3.micro  = $15/月
存储:          20GB      = $2/月
Data Transfer: 100GB/月  = $5/月
────────────────────────────────
月度成本：                 ~$37/月
```

### 生产级别（推荐）

```
EC2:           t3.medium = $35/月
RDS:           t3.small  = $30/月
ALB:           1 个      = $16/月
存储:          100GB     = $10/月
Data Transfer: 500GB/月  = $25/月
────────────────────────────────
月度成本：                 ~$116/月
```

---

## 🆘 快速排查

如果部署出现问题，依次检查：

```
1. 是否成功登录 EC2?
   └─► 检查：密钥权限、安全组、EC2 IP

2. EC2 上是否安装了 Java?
   └─► 检查：java -version，运行 deploy.sh

3. JAR 文件是否存在?
   └─► 检查：ls -lah /opt/app/*.jar，SCP 上传

4. MySQL 是否可连接?
   └─► 检查：mysql 命令，RDS 安全组设置

5. 应用是否启动成功?
   └─► 检查：sudo systemctl status，查看日志

6. 能否访问应用?
   └─► 检查：curl http://ip:8080，防火墙规则
```

---

## 📚 完整部署时间表

| 步骤 | 时间 | 内容 |
|------|------|------|
| **准备** | 5分钟 | 阅读本文档和 AWS_SUMMARY |
| **创建 EC2** | 3分钟 | AWS 控制台启动实例 |
| **创建 RDS** | 5分钟 | AWS 控制台创建数据库 |
| **连接 & 部署** | 5分钟 | SSH 连接，运行 deploy.sh |
| **上传应用** | 2分钟 | SCP 上传 JAR |
| **配置&启动** | 3分钟 | 编辑配置，启动应用 |
| **测试验证** | 5分钟 | API 测试，数据验证 |
| **总计** | ~30分钟 | 完整部署 |

---

## 🎓 部署后学习

完成基础部署后，建议学习：

1. **AWS 架构最佳实践**
   - VPC 和子网设计
   - 安全组和 NACL
   - IAM 身份和权限

2. **扩展性**
   - Auto Scaling Groups
   - ElastiCache（Redis）
   - CloudFront（CDN）

3. **可靠性**
   - RDS 多可用区
   - 跨区域副本
   - 灾难恢复计划

4. **监控和日志**
   - CloudWatch 指标
   - X-Ray 追踪
   - ELK Stack（可选）

5. **CI/CD 流程**
   - CodePipeline
   - CodeDeploy
   - GitHub Actions 集成

---

## 📞 有用的资源链接

- **AWS 文档**: https://docs.aws.amazon.com/
- **EC2 用户指南**: https://docs.aws.amazon.com/ec2/
- **RDS 用户指南**: https://docs.aws.amazon.com/rds/
- **Spring Boot 文档**: https://spring.io/projects/spring-boot
- **MySQL 8.0 文档**: https://dev.mysql.com/doc/

---

## 版本信息

```
应用版本:           1.0.0
Spring Boot:        3.1.5
Java:               21
MySQL:              8.0+
部署指南更新日期:    2026-04-06
```

---

**✨ 现在你已经拥有完整的 AWS 部署方案！祝部署顺利！**
