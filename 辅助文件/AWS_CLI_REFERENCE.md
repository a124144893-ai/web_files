# AWS CLI 命令参考（可选，高级用户）

## 安装 AWS CLI v2

### Windows
```powershell
# 下载并安装
https://awscli.amazonaws.com/AWSCLIV2.msi

# 验证
aws --version
```

### macOS
```bash
curl "https://awscli.amazonaws.com/awscli-exe-macos.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

### Linux
```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

## 配置 AWS CLI

```bash
aws configure

# 输入：
# AWS Access Key ID: 从 IAM 获取
# AWS Secret Access Key: 从 IAM 获取
# Default region name: us-east-1 (或你的地区)
# Default output format: json
```

---

## EC2 操作命令

### 创建 EC2 实例
```bash
aws ec2 run-instances \
  --image-id ami-0c02fb55956c7d316 \
  --instance-type t3.small \
  --key-name movie-booking-key \
  --security-groups movie-booking-sg \
  --subnet-id subnet-xxxxx \
  --associate-public-ip-address \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=MovieBooking}]' \
  --region us-east-1

# 返回示例输
# "InstanceId": "i-0f598c0d8ec0d6a74"
```

### 列出所有 EC2 实例
```bash
aws ec2 describe-instances \
  --filters "Name=instance-state-name,Values=running" \
  --query 'Reservations[*].Instances[*].[InstanceId,PublicIpAddress,PrivateIpAddress,State.Name]' \
  --output table
```

### 获取特定实例的公有 IP
```bash
aws ec2 describe-instances \
  --instance-ids i-0f598c0d8ec0d6a74 \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --output text
```

### 启动实例
```bash
aws ec2 start-instances --instance-ids i-0f598c0d8ec0d6a74
```

### 停止实例
```bash
aws ec2 stop-instances --instance-ids i-0f598c0d8ec0d6a74
```

### 终止实例（删除）
```bash
aws ec2 terminate-instances --instance-ids i-0f598c0d8ec0d6a74
```

### 创建安全组
```bash
aws ec2 create-security-group \
  --group-name movie-booking-sg \
  --description "Security group for Movie Booking App" \
  --vpc-id vpc-xxxxx

# 返回 GroupId
```

### 授权安全组入站规则
```bash
# SSH (22)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 22 \
  --cidr 0.0.0.0/0

# HTTP (80)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# HTTPS (443)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0

# 应用端口 (8080)
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0
```

### 创建密钥对
```bash
aws ec2 create-key-pair \
  --key-name movie-booking-key \
  --query 'KeyMaterial' \
  --output text > movie-booking-key.pem

# 修改权限
chmod 400 movie-booking-key.pem
```

### 分配 Elastic IP
```bash
aws ec2 allocate-address --domain vpc

# 关联到实例
aws ec2 associate-address \
  --instance-id i-0f598c0d8ec0d6a74 \
  --allocation-id eipalloc-xxxxx
```

---

## RDS 操作命令

### 创建 RDS MySQL 数据库
```bash
aws rds create-db-instance \
  --db-instance-identifier movie-booking-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0.35 \
  --master-username admin \
  --master-user-password 'YourStrongPassword123!' \
  --allocated-storage 20 \
  --db-name movie_db \
  --backup-retention-period 7 \
  --storage-type gp3 \
  --storage-encrypted \
  --publicly-accessible false \
  --enable-cloudwatch-logs-exports error,general,slowquery
```

### 列出 RDS 实例
```bash
aws rds describe-db-instances \
  --query 'DBInstances[*].[DBInstanceIdentifier,DBInstanceStatus,Endpoint.Address,Endpoint.Port]' \
  --output table
```

### 获取 RDS 端点
```bash
aws rds describe-db-instances \
  --db-instance-identifier movie-booking-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

### 修改数据库主密码
```bash
aws rds modify-db-instance \
  --db-instance-identifier movie-booking-db \
  --master-user-password 'NewStrongPassword123!' \
  --apply-immediately
```

### 创建数据库快照
```bash
aws rds create-db-snapshot \
  --db-instance-identifier movie-booking-db \
  --db-snapshot-identifier movie-booking-db-backup-2024-04-06
```

### 列出快照
```bash
aws rds describe-db-snapshots \
  --query 'DBSnapshots[*].[DBSnapshotIdentifier,SnapshotCreateTime,DBInstanceIdentifier]' \
  --output table
```

### 删除 RDS 实例
```bash
aws rds delete-db-instance \
  --db-instance-identifier movie-booking-db \
  --skip-final-snapshot
  # 不跳过快照：移除 --skip-final-snapshot 参数
```

---

## S3 操作（用于存储 JAR）

### 创建 S3 桶
```bash
aws s3 mb s3://movie-booking-app-bucket

# 启用版本控制
aws s3api put-bucket-versioning \
  --bucket movie-booking-app-bucket \
  --versioning-configuration Status=Enabled
```

### 上传文件到 S3
```bash
aws s3 cp movie-booking-app-1.0.0.jar \
  s3://movie-booking-app-bucket/releases/v1.0.0/
```

### 列出 S3 文件
```bash
aws s3 ls s3://movie-booking-app-bucket/
```

### 下载文件
```bash
aws s3 cp s3://movie-booking-app-bucket/releases/v1.0.0/movie-booking-app-1.0.0.jar .
```

---

## Load Balancer 操作

### 创建 ALB
```bash
aws elbv2 create-load-balancer \
  --name movie-booking-alb \
  --subnets subnet-xxxxx subnet-yyyyy \
  --security-groups sg-xxxxx \
  --scheme internet-facing \
  --type application \
  --ip-address-type ipv4
```

### 创建目标组
```bash
aws elbv2 create-target-group \
  --name movie-booking-tg \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxxxx \
  --health-check-path /movies/hot \
  --health-check-interval-seconds 30 \
  --health-check-timeout-seconds 5
```

### 注册目标（EC2 实例）
```bash
aws elbv2 register-targets \
  --target-group-arn arn:aws:elasticloadbalancing:... \
  --targets Id=i-0f598c0d8ec0d6a74
```

### 创建监听器
```bash
aws elbv2 create-listener \
  --load-balancer-arn arn:aws:elasticloadbalancing:... \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:...
```

---

## CloudWatch 操作

### 创建告警
```bash
# CPU 使用率告警（超过 70%）
aws cloudwatch put-metric-alarm \
  --alarm-name high-cpu-usage \
  --alarm-description "Alert when CPU exceeds 70%" \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 300 \
  --threshold 70 \
  --comparison-operator GreaterThanThreshold \
  --dimensions Name=InstanceId,Value=i-0f598c0d8ec0d6a74
```

### 列出告警
```bash
aws cloudwatch describe-alarms --output table
```

---

## IAM 操作（用于 EC2 访问 S3、RDS）

### 创建 IAM 角色
```bash
aws iam create-role \
  --role-name MovieBookingEC2Role \
  --assume-role-policy-document file://trust-policy.json
```

trust-policy.json 内容：
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

### 创建 IAM 策略
```bash
aws iam create-policy \
  --policy-name MovieBookingS3RDSPolicy \
  --policy-document file://policy.json
```

policy.json 内容：
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::movie-booking-app-bucket",
        "arn:aws:s3:::movie-booking-app-bucket/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "rds-db:connect"
      ],
      "Resource": "arn:aws:rds:*:*:db/movie-booking-db"
    }
  ]
}
```

---

## 有用的实用命令

### 检查 AWS 账户信息
```bash
aws sts get-caller-identity
```

### 列出所有地区
```bash
aws ec2 describe-regions --output table
```

### 列出所有可用区
```bash
aws ec2 describe-availability-zones --region us-east-1 --output table
```

### 获取账单
```bash
aws ce get-cost-and-usage \
  --time-period Start=2024-04-01,End=2024-04-06 \
  --granularity MONTHLY \
  --metrics BlendedCost \
  --group-by Type=DIMENSION,Key=SERVICE
```

---

## 常用 AWS CLI 别名

将以下添加到 `~/.bashrc` 或 `~/.zshrc`：

```bash
alias awed='aws ec2 describe-instances'
alias awls='aws s3 ls'
alias awdb='aws rds describe-db-instances'

# 快速启动/停止实例
start_instance() { aws ec2 start-instances --instance-ids $1; }
stop_instance() { aws ec2 stop-instances --instance-ids $1; }
```

---

## 最佳实践

1. **始终使用 IAM 用户而非根用户**
2. **启用 MFA（多因素认证）**
3. **定期轮换访问密钥**
4. **使用 VPC 和安全组隔离资源**
5. **启用 CloudTrail 审计日志**
6. **定期备份数据库**
7. **设置预算告警**
8. **对敏感数据启用加密**
