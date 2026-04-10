#!/bin/bash
set -e

# AWS EC2 部署脚本 - 运行一次即可完成环境配置
# 使用方法: chmod +x deploy.sh && ./deploy.sh

echo "======================================"
echo "电影订票系统 AWS 部署脚本"
echo "======================================"

# 1. 更新系统
echo "✓ 更新系统包..."
sudo yum update -y

# 2. 安装 Java 21
echo "✓ 安装 Java 21..."
sudo yum install java-21-amazon-corretto -y
java -version

# 3. 创建应用目录
echo "✓ 创建应用目录..."
sudo mkdir -p /opt/app
sudo chmod 755 /opt/app
sudo chown ec2-user:ec2-user /opt/app

# 4. 下载应用（从 S3）
# 修改为你的实际 S3 路径
# echo "✓ 从 S3 下载应用..."
# aws s3 cp s3://your-bucket/movie-booking-app-1.0.0.jar /opt/app/

# 5. 或者通过 SCP 上传后，继续执行以下步骤

# 6. 修改配置文件
echo "✓ 配置应用..."
cat > /opt/app/application-prod.yml <<'EOF'
spring:
  application:
    name: movie-booking-app
  datasource:
    url: jdbc:mysql://RDS_ENDPOINT:3306/movie_db?useSSL=true&serverTimezone=UTC&characterEncoding=utf8&useUnicode=true
    username: RDS_USERNAME
    password: RDS_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        show-sql: false
  sql:
    init:
      encoding: utf-8

server:
  port: 8080
  compression:
    enabled: true

jwt:
  secret: movie-booking-secret-key-change-in-production-environment-please-use-strong-key-at-least-256-bits
  expiration: 86400000

logging:
  level:
    root: INFO
    com.moviebooking: INFO
EOF

echo "⚠️  请手动修改 /opt/app/application-prod.yml 中的:"
echo "   - RDS_ENDPOINT: 你的 RDS 端点"
echo "   - RDS_USERNAME: RDS 用户名"
echo "   - RDS_PASSWORD: RDS 密码"

# 7. 创建启动脚本
echo "✓ 创建启动脚本..."
cat > /opt/app/start.sh <<'EOF'
#!/bin/bash
cd /opt/app
java -jar movie-booking-app-1.0.0.jar \
  --spring.config.location=classpath:application.yml,file:/opt/app/application-prod.yml \
  -Dfile.encoding=UTF-8 \
  -Xmx512m -Xms256m
EOF

chmod +x /opt/app/start.sh

# 8. 创建 systemd 服务
echo "✓ 创建 systemd 服务..."
sudo tee /etc/systemd/system/movie-booking.service > /dev/null <<EOF
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
Environment="JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto"

[Install]
WantedBy=multi-user.target
EOF

# 9. 启用和启动服务
echo "✓ 启用服务..."
sudo systemctl daemon-reload
sudo systemctl enable movie-booking

echo ""
echo "======================================"
echo "✓ 部署前准备完成！"
echo "======================================"
echo ""
echo "下一步操作："
echo "1. 上传 JAR 文件到 /opt/app/"
echo "   scp -i key.pem movie-booking-app-1.0.0.jar ec2-user@YOUR_IP:/opt/app/"
echo ""
echo "2. 修改配置文件中的 RDS 信息："
echo "   sudo vim /opt/app/application-prod.yml"
echo ""
echo "3. 启动应用："
echo "   sudo systemctl start movie-booking"
echo ""
echo "4. 查看日志："
echo "   sudo journalctl -u movie-booking -f"
echo ""
