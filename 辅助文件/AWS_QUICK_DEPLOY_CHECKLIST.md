# AWS 快速部署清单

## 第1阶段：AWS 基础设施准备（控制台操作）

### 1.1 创建 EC2 实例
```
AWS 控制台 → EC2 → 实例 → 启动新实例

配置步骤：
1. 选择 AMI：Amazon Linux 2 AMI（或 Ubuntu 22.04 LTS）
2. 实例类型：t3.small（免费套层 t2.micro 也可）
3. 网络：
   - VPC：default（或创建新的）
   - 子网：公有子网
   - 自动分配公有 IP：启用
4. 存储: EBS 20GB (gp3)
5. 安全组：创建新安全组 "movie-booking-sg"
   入站规则：
   - 类型: SSH，来源: 0.0.0.0/0，端口: 22
   - 类型: HTTP，来源: 0.0.0.0/0，端口: 80
   - 类型: HTTPS，来源: 0.0.0.0/0，端口: 443
   - 类型: 自定义 TCP，来源: 0.0.0.0/0，端口: 8080
6. 密钥对：创建新密钥对 "movie-booking-key"
   ⚠️ 下载并保管 .pem 文件（非常重要！）
7. 启动实例

获取信息：
- 记录 EC2 的公有 IP: ______________
- 记录 EC2 的私有 IP: ______________
```

### 1.2 创建 RDS MySQL 数据库
```
AWS 控制台 → RDS → 数据库 → 创建数据库

配置步骤：
1. 数据库引擎：MySQL 8.0.35+
2. 模板：可用层（免费） 或 开发/测试
3. DB 实例标识符：movie-booking-db
4. 主用户名：admin
5. 主密码：生成强密码（至少 20 个字符）
   ⚠️ 妥善保管密码！
6. DB 实例类别：db.t3.micro（免费） 或 db.t3.small
7. 存储：
   - 分配的存储：20GB
   - 存储类型：gp3
   - 启用存储自动扩展：是
8. 可用性和耐久性：
   - 生产环境：启用多可用区
   - 开发环境：不启用
9. 连接性：
   - VPC：与 EC2 相同
   - 公有可访问性：否（安全）
10. 初始数据库名称：movie_db
11. 创建数据库

获取信息：
- 记录 RDS 端点：__________________ (例: movie-booking-db.xyz.us-east-1.rds.amazonaws.com)
- 记录端口：3306
- 记录用户名：admin
- 记录密码：______________
- 记录数据库名：movie_db
```

### 1.3 配置 RDS 安全组
```
AWS 控制台 → EC2 → 安全组 → RDS 安全组

入站规则：
- 类型: MySQL/Aurora，来源: sg-xxxx (movie-booking-sg)，端口: 3306
  或 来源: 0.0.0.0/0（仅用于临时调试）

出站规则：保持默认（全部允许）
```

---

## 第2阶段：EC2 应用部署

### 2.1 连接到 EC2
```bash
# 修改权限
chmod 400 movie-booking-key.pem

# 连接
ssh -i movie-booking-key.pem ec2-user@你的EC2公有IP

# 或者 Ubuntu
ssh -i movie-booking-key.pem ubuntu@你的EC2公有IP
```

### 2.2 准备应用环境
```bash
# 上传 deploy.sh 脚本
scp -i movie-booking-key.pem deploy.sh ec2-user@你的EC2公有IP:~/

# SSH 连接到 EC2
ssh -i movie-booking-key.pem ec2-user@你的EC2公有IP

# 运行部署脚本
bash ~/deploy.sh
```

### 2.3 上传应用 JAR
```bash
# 从本地上传
scp -i movie-booking-key.pem C:\web_project\movie-booking-backend\target\movie-booking-app-1.0.0.jar ec2-user@你的EC2公有IP:/opt/app/

# 验证上传
ssh -i movie-booking-key.pem ec2-user@你的EC2公有IP
ls -lah /opt/app/
```

### 2.4 初始化数据库
```bash
# 通过 EC2 连接 RDS MySQL
mysql -h your-rds-endpoint.amazonaws.com -u admin -p

# 或一步到位（需要先上传 data.sql）
mysql -h your-rds-endpoint.amazonaws.com -u admin -p movie_db < data.sql
```

#### 上传数据初始化脚本
```bash
scp -i movie-booking-key.pem C:\web_project\movie-booking-backend\src\main\resources\data.sql ec2-user@你的EC2公有IP:/opt/app/

# SSH 到 EC2 后执行
mysql -h RDS端点 -u admin -p movie_db < /opt/app/data.sql
```

### 2.5 配置应用
```bash
# SSH 到 EC2
ssh -i movie-booking-key.pem ec2-user@你的EC2公有IP

# 编辑应用配置
sudo vim /opt/app/application-prod.yml

# 修改：
# - RDS_ENDPOINT 改为实际 RDS 端点
# - RDS_USERNAME 改为 admin
# - RDS_PASSWORD 改为实际密码
```

### 2.6 启动应用
```bash
# 启动服务
sudo systemctl start movie-booking

# 检查状态
sudo systemctl status movie-booking

# 查看日志（实时）
sudo journalctl -u movie-booking -f
```

---

## 第3阶段：测试验证

### 3.1 测试基本连接
```bash
# 从本地测试（替换成你的 EC2 IP）
curl http://你的EC2公有IP:8080/movies/hot

# 应该返回电影列表 JSON
```

### 3.2 测试登录
```bash
curl -X POST http://你的EC2公有IP:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"lisi","password":"admin123"}'

# 应该返回 JWT token
```

### 3.3 查看应用日志错误
```bash
sudo journalctl -u movie-booking -n 100
```

---

## 第4阶段：配置负载均衡器（可选但推荐）

### 4.1 创建 Application Load Balancer
```
AWS 控制台 → EC2 → 负载均衡器 → 创建负载均衡器

选择 Application Load Balancer

步骤 1：基本配置
- 名称：movie-booking-alb
- IP 地址类型：IPv4
- 方案：面向互联网

步骤 2：网络映射
- VPC：选择 EC2 所在 VPC
- 子网：选择公有子网

步骤 3：安全组
- movie-booking-sg

步骤 4：监听器和路由
- 协议：HTTP，端口：80
- 目标类型：实例
- 创建目标组：movie-booking-tg
  - 协议：HTTP，端口：8080
  - 健康检查：
    - 路径：/movies/hot
    - 间隔：30 秒
    - 超时：5 秒
- 注册目标：选择你的 EC2 实例

步骤 5：创建
```

### 4.2 获取 ALB DNS
```
负载均衡器 → 目标 DNS：movie-booking-alb-xxxx.elb.us-east-1.amazonaws.com

现在可用：http://movie-booking-alb-xxxx.elb.us-east-1.amazonaws.com/movies/hot
```

---

## 第5阶段：配置 HTTPS（可选）

### 5.1 申请 SSL 证书
```
AWS 控制台 → Certificate Manager → 申请证书
- 输入域名：your-domain.com
- 验证：选择 DNS 验证
- 妥善保管证书 ARN
```

### 5.2 配置 HTTPS 监听器
```
ALB → 监听器 → 添加监听器
- 协议：HTTPS，端口：443
- 默认 SSL 证书：选择申请的证书
- 默认操作：转发到 movie-booking-tg
```

---

## 常见问题排查

### Q: 无法连接实例
**A:** 检查安全组的 SSH (22) 端口是否开放

### Q: 无法连接数据库
**A:** 
1. 检查 RDS 安全组是否允许来自 EC2 的 3306 端口
2. 检查 RDS 端点是否正确
3. 检查用户名和密码是否正确

### Q: 应用无法启动
**A:** 
1. 查看日志：`sudo journalctl -u movie-booking -n 50`
2. 检查 Java 是否正确安装：`java -version`
3. 检查 JAR 文件是否存在：`ls -lah /opt/app/*.jar`

### Q: 返回 502 Bad Gateway
**A:** 
1. 检查应用是否运行：`sudo systemctl status movie-booking`
2. 检查 8080 端口是否监听：`sudo netstat -tulpn | grep 8080`
3. 检查 ALB 目标健康：AWS 控制台 → 目标组 → 健康检查

---

## 成本优化建议

1. **使用 t3.micro**（免费套层）而非 t3.small
2. **关闭不需要的服务**（如 Redis）
3. **定期檢查 CloudWatch** 监控成本
4. **使用 Reserved Instances** 如果长期运行（可省 30-50%）
5. **定期备份管理** 避免存储积压

---

## 下一步

- [ ] 配置域名（Route 53 或外部 DNS）
- [ ] 启用自动扩展（Auto Scaling）
- [ ] 配置备份策略
- [ ] 启用 CloudWatch 告警
- [ ] 考虑使用 Docker + ECS 进行更简单的部署
