# 电影订票系统 AWS 部署指南

## 整体架构

```
互联网用户
    ↓
Amazon Route 53 (DNS) / 申请域名
    ↓
AWS Application Load Balancer (ALB) 
    ↓
EC2 实例 (Spring Boot 应用)
    ↓
Amazon RDS MySQL 数据库
```

## 部署方案步骤

### 第一步：准备 AWS 资源

#### 1.1 准备 VPC 和安全组
- 创建或选择 VPC
- 创建安全组，允许：
  - HTTP (80) 和 HTTPS (443) 入站流量
  - SSH (22) 用于远程管理
  - MySQL (3306) 仅限内部访问

#### 1.2 创建 EC2 实例
- **AMI**: Amazon Linux 2 或 Ubuntu 22.04 LTS
- **实例类型**: t3.small 或 t3.medium（根据预期流量）
- **存储**: 至少 20GB EBS (gp3)
- **安全组**: 允许入站 22, 80, 443 端口

#### 1.3 创建 RDS MySQL 数据库（推荐方案）
- **引擎**: MySQL 8.0+
- **实例类型**: db.t3.micro（免费套层）或 db.t3.small
- **存储**: 20GB gp2
- **多可用区**: 否（开发）/ 是（生产）
- **数据库名**: movie_db
- **用户名**: admin
- **密码**: 生成强密码并妥善保管

**或者** 在 EC2 上自行安装 MySQL 8.4

### 第二步：配置 EC2 实例

#### 2.1 连接到 EC2
```bash
ssh -i "your-key-pair.pem" ec2-user@your-instance-public-ip
# 或 Ubuntu
ssh -i "your-key-pair.pem" ubuntu@your-instance-public-ip
```

#### 2.2 安装依赖
```bash
# 更新系统
sudo yum update -y  # Amazon Linux 2
# 或
sudo apt update && sudo apt upgrade -y  # Ubuntu

# 安装 Java 21
sudo yum install java-21-amazon-corretto -y
# 或
sudo apt install openjdk-21-jdk -y

# 验证 Java
java -version
```

#### 2.3 下载应用 JAR 包
选择以下方式之一：

**方式 A：通过 S3 上传**
1. 上传 JAR 到 S3: `movie-booking-app-1.0.0.jar`
2. 在 EC2 上下载：
```bash
cd /opt/app
aws s3 cp s3://your-bucket/movie-booking-app-1.0.0.jar .
```

**方式 B：直接上传（使用 SCP）**
```bash
scp -i "your-key-pair.pem" /path/to/movie-booking-app-1.0.0.jar ec2-user@your-instance-public-ip:/opt/app/
```

### 第三步：配置应用

#### 3.1 修改 application.yml
根据 RDS 数据库信息修改：
```yaml
spring:
  datasource:
    url: jdbc:mysql://your-rds-endpoint:3306/movie_db?useSSL=true&serverTimezone=UTC
    username: admin
    password: your-rds-password
    # 其他配置保持不变
```

#### 3.2 创建数据库和表
```bash
# 从 EC2 连接到 RDS
mysql -h your-rds-endpoint -u admin -p

# 执行 SQL 初始化脚本（使用 data.sql）
mysql -h your-rds-endpoint -u admin -p movie_db < /opt/app/data.sql
```

### 第四步：部署应用

#### 4.1 创建启动脚本
```bash
sudo vim /opt/app/start.sh
```

内容：
```bash
#!/bin/bash
cd /opt/app
java -jar movie-booking-app-1.0.0.jar \
  -Dserver.port=8080 \
  -Dfile.encoding=UTF-8 \
  -Xmx512m -Xms256m
```

#### 4.2 赋予执行权限
```bash
chmod +x /opt/app/start.sh
```

#### 4.3 创建 systemd 服务（推荐）
```bash
sudo vim /etc/systemd/system/movie-booking.service
```

内容：
```ini
[Unit]
Description=Movie Booking Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/app
ExecStart=/opt/app/start.sh
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

#### 4.4 启动服务
```bash
sudo systemctl enable movie-booking
sudo systemctl start movie-booking
sudo systemctl status movie-booking
```

#### 4.5 查看日志
```bash
sudo journalctl -u movie-booking -f
# 或
tail -f /var/log/application.log
```

### 第五步：配置负载均衡器（可选但推荐）

#### 5.1 创建 Application Load Balancer
- 目标组: 指向 EC2 实例，端口 8080
- 监听器: 80 → 8080（HTTP）
- 配置健康检查: `/movies/hot` (GET)

#### 5.2 配置 HTTPS（使用 SSL 证书）
- 申请 AWS Certificate Manager 证书
- 配置监听器: 443 → 8080（HTTPS）

### 第六步：域名配置（可选）

#### 6.1 使用 Route 53
在 AWS Route 53 中：
- 创建托管区域
- 添加 A 记录指向 ALB DNS 或 EC2 Elastic IP

### 第七步：监控和维护

#### 7.1 CloudWatch 监控
- 设置 EC2 CPU、内存、磁盘告警
- 监控 RDS 连接数和性能指标

#### 7.2 备份策略
- RDS 自动备份: 7-35 天
- 定期导出数据库快照

## 成本估算（月度，亚太地区）

| 服务 | 类型 | 成本 |
|------|------|------|
| **EC2** | t3.small | ~15 USD |
| **RDS MySQL** | db.t3.micro | ~15 USD |
| **Data Transfer** | ~10GB/月 | ~1 USD |
| **ALB** (可选) | - | ~16 USD |
| **总计** | | ~31-47 USD/月 |

## 部署检查清单

- [ ] AWS 账户已创建，有足够额度
- [ ] VPC 和安全组已配置
- [ ] EC2 实例已启动
- [ ] RDS 数据库集群已创建
- [ ] Java 21 已安装
- [ ] 应用 JAR 已上传到 EC2
- [ ] application.yml 已根据 RDS 修改
- [ ] 数据库初始化脚本已执行
- [ ] 应用服务已启动并运行
- [ ] 可从外部访问应用（通过 EC2 IP 或 ALB DNS）
- [ ] 日志正常，无错误

## 故障排查

### 应用无法启动
```bash
# 查看详细错误
sudo journalctl -u movie-booking -n 50
# 检查 Java 进程
ps aux | grep java
```

### 无法连接数据库
```bash
# 检查 RDS 安全组允许 52.79.0.0/16 访问
# 检查安全组入站规则是否包含 3306
mysql -h your-rds-endpoint -u admin -p
```

### 端口占用
```bash
# 查看 8080 端口占用
sudo lsof -i :8080
# 杀死占用进程
sudo kill -9 <PID>
```

## 下一步优化

1. **使用 Docker**: 容器化应用，更便于部署和扩展
2. **ECS/Fargate**: 完全托管容器服务
3. **Auto Scaling**: 根据流量自动扩展实例数
4. **CloudFront**: CDN 加速前端资源
5. **RDS Proxy**: 提高数据库连接效率
