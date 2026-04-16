-- ===========================================
-- 电影订票系统数据库初始化脚本
-- 用于 AWS RDS MySQL 8.0+ 部署
-- 生成时间: 2026-04-08
-- ===========================================

-- 设置字符集和字符集排序规则
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_DATABASE = utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS movie_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE movie_db;

-- 禁用外键约束检查（便于数据插入）
SET FOREIGN_KEY_CHECKS = 0;

-- ===========================================
-- 1. 创建用户表 (users)
-- ===========================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    avatar_url VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    email VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    password VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    phone VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    role ENUM('ADMIN','USER') NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    username VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_r43af9ap4edm43mmtq01oddj6 (username),
    UNIQUE KEY UK_6dotkott2kjsp8vw4d0m25fb7 (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 2. 创建电影表 (movies)
-- ===========================================
DROP TABLE IF EXISTS movies;
CREATE TABLE movies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cast VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    description VARCHAR(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    director VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    duration_minutes INT DEFAULT NULL,
    is_hot BIT(1) NOT NULL,
    poster_url VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    rating DECIMAL(3,1) DEFAULT NULL,
    release_date DATE NOT NULL,
    title VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 3. 创建放映时间表 (showtimes)
-- ===========================================
DROP TABLE IF EXISTS showtimes;
CREATE TABLE showtimes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    available_seats INT NOT NULL,
    cinema_hall VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    created_at DATETIME(6) NOT NULL,
    price DECIMAL(10,2) DEFAULT NULL,
    start_time DATETIME(6) NOT NULL,
    total_seats INT NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    movie_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY FKeltpyuei1d5g3n6ikpsjwwil6 (movie_id),
    CONSTRAINT FKeltpyuei1d5g3n6ikpsjwwil6 FOREIGN KEY (movie_id) REFERENCES movies (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 4. 创建预约表 (bookings)
-- ===========================================
DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_time DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    seat_count INT NOT NULL,
    seat_numbers VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    status ENUM('CANCELED','COMPLETED','CONFIRMED','PENDING') NOT NULL,
    total_price DECIMAL(10,2) DEFAULT NULL,
    updated_at DATETIME(6) NOT NULL,
    showtime_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY FKkt7t3pjw1p3fl7o8s9w0n5w2 (showtime_id),
    KEY FKkt7t3pjw1p3fl7o8s9w0n5w2a (user_id),
    CONSTRAINT FKkt7t3pjw1p3fl7o8s9w0n5w2 FOREIGN KEY (showtime_id) REFERENCES showtimes (id),
    CONSTRAINT FKkt7t3pjw1p3fl7o8s9w0n5w2a FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 5. 创建电影类型关联表 (movie_genres)
-- ===========================================
DROP TABLE IF EXISTS movie_genres;
CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL,
    genre VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    KEY FK6x2ww1k6y5b8q8q8q8q8q8q8q (movie_id),
    CONSTRAINT FK6x2ww1k6y5b8q8q8q8q8q8q8q FOREIGN KEY (movie_id) REFERENCES movies (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================
-- 插入初始化数据
-- ===========================================

-- 插入用户数据
INSERT INTO users (id, avatar_url, created_at, email, password, phone, role, updated_at, username) VALUES
(1, NULL, '2026-04-06 17:05:39.000000', 'admin@email.com', '123456', '13800138000', 'ADMIN', '2026-04-06 17:05:39.000000', 'admin');

-- 插入电影数据
INSERT INTO movies (id, cast, created_at, description, director, duration_minutes, is_hot, poster_url, rating, release_date, title, updated_at) VALUES
(1, '蒂莫西·查拉梅,赞达亚', '2026-04-06 17:21:43.000000', '史诗级科幻大作，展现宇宙政治与人性抉择的碰撞', '丹尼斯·维伦纽瓦', 166, 1, 'https://example.com/dune2.jpg', 8.5, '2024-12-20', '沙丘2', '2026-04-06 17:21:43.000000'),
(2, '吴京,李雪健', '2026-04-06 17:21:43.000000', '人类为生存而进行的终极逃亡之旅', '张艺谋', 173, 1, 'https://example.com/wandering_earth2.jpg', 8.2, '2024-11-15', '流浪地球2', '2026-04-06 17:21:43.000000'),
(3, '基里安·墨菲,小罗伯特·唐尼', '2026-04-06 17:21:43.000000', '揭秘原子弹之父的人生故事与伦理困境', '克里斯托弗·诺兰', 180, 1, 'https://example.com/oppenheimer.jpg', 8.8, '2024-10-01', '奥本海默', '2026-04-06 17:21:43.000000'),
(4, '葛优,宋丹丹', '2026-04-06 17:21:43.000000', '平凡生活中的不凡故事', '葛优', 112, 0, 'https://example.com/paradise.jpg', 7.6, '2024-09-28', '乐园', '2026-04-06 17:21:43.000000'),
(5, '配音演员', '2026-04-06 17:21:43.000000', '史诗级动画冒险，探索神秘海底世界', '田晓鹏', 128, 0, 'https://example.com/deep_sea.jpg', 7.9, '2024-08-15', '深海', '2026-04-06 17:21:43.000000');

-- 插入放映时间数据
INSERT INTO showtimes (id, available_seats, cinema_hall, created_at, price, start_time, total_seats, updated_at, movie_id) VALUES
(1, 85, 'IMAX 1号厅', '2026-04-06 17:21:43.000000', 68.00, '2026-04-07 10:00:00.000000', 120, '2026-04-06 17:21:43.000000', 1),
(2, 45, 'IMAX 1号厅', '2026-04-06 17:21:43.000000', 68.00, '2026-04-07 14:30:00.000000', 120, '2026-04-06 17:21:43.000000', 1),
(3, 30, '4DX 厅', '2026-04-06 17:21:43.000000', 78.00, '2026-04-07 18:00:00.000000', 100, '2026-04-06 17:21:43.000000', 1),
(4, 100, '4DX 厅', '2026-04-06 17:21:43.000000', 78.00, '2026-04-07 21:00:00.000000', 100, '2026-04-06 17:21:43.000000', 1),
(5, 95, 'IMAX 2号厅', '2026-04-06 17:21:43.000000', 68.00, '2026-04-07 10:30:00.000000', 120, '2026-04-06 17:21:43.000000', 2),
(6, 60, '激光厅', '2026-04-06 17:21:43.000000', 75.00, '2026-04-07 15:00:00.000000', 150, '2026-04-06 17:21:43.000000', 2),
(7, 25, '激光厅', '2026-04-06 17:21:43.000000', 75.00, '2026-04-07 19:30:00.000000', 150, '2026-04-06 17:21:43.000000', 2),
(8, 80, '标准2厅', '2026-04-06 17:21:43.000000', 45.00, '2026-04-07 11:00:00.000000', 100, '2026-04-06 17:21:43.000000', 3),
(9, 70, '标准2厅', '2026-04-06 17:21:43.000000', 45.00, '2026-04-07 16:00:00.000000', 100, '2026-04-06 17:21:43.000000', 3),
(10, 40, '标准3厅', '2026-04-06 17:21:43.000000', 45.00, '2026-04-07 20:00:00.000000', 100, '2026-04-06 17:21:43.000000', 3),
(11, 92, '标准1厅', '2026-04-06 17:21:43.000000', 39.00, '2026-04-07 12:00:00.000000', 100, '2026-04-06 17:21:43.000000', 4),
(12, 100, '标准1厅', '2026-04-06 17:21:43.000000', 39.00, '2026-04-07 17:00:00.000000', 100, '2026-04-06 17:21:43.000000', 4),
(13, 72, '儿童厅', '2026-04-06 17:21:43.000000', 35.00, '2026-04-07 13:00:00.000000', 80, '2026-04-06 17:21:43.000000', 5),
(14, 68, '儿童厅', '2026-04-06 17:21:43.000000', 35.00, '2026-04-07 19:00:00.000000', 80, '2026-04-06 17:21:43.000000', 5);

-- 插入预约数据
INSERT INTO bookings (id, booking_time, created_at, seat_count, seat_numbers, status, total_price, updated_at, showtime_id, user_id) VALUES
(1, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 2, 'A1,A2', 'CONFIRMED', 136.00, '2026-04-06 17:21:43.000000', 1, 2),
(2, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 1, 'A3', 'CONFIRMED', 68.00, '2026-04-06 17:21:43.000000', 1, 2),
(3, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 3, 'B1,B2,B3', 'CONFIRMED', 234.00, '2026-04-06 17:21:43.000000', 3, 3),
(4, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 2, 'C1,C2', 'CONFIRMED', 136.00, '2026-04-06 17:21:43.000000', 5, 4),
(5, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 1, 'D1', 'PENDING', 75.00, '2026-04-06 17:21:43.000000', 6, 3),
(6, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 4, 'E1,E2,E3,E4', 'CONFIRMED', 180.00, '2026-04-06 17:21:43.000000', 8, 2),
(7, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 2, 'F1,F2', 'CONFIRMED', 90.00, '2026-04-06 17:21:43.000000', 10, 4),
(8, '2026-04-06 17:21:43.000000', '2026-04-06 17:21:43.000000', 1, 'G1', 'CANCELED', 35.00, '2026-04-06 17:21:43.000000', 13, 3);

-- 启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================
-- 验证数据完整性
-- ===========================================

-- 验证表创建成功
SELECT 'Tables created successfully:' AS status;
SHOW TABLES;

-- 验证数据插入成功
SELECT 'Data verification:' AS status;
SELECT 'Users count:' AS info, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Movies count:', COUNT(*) FROM movies
UNION ALL
SELECT 'Showtimes count:', COUNT(*) FROM showtimes
UNION ALL
SELECT 'Bookings count:', COUNT(*) FROM bookings
UNION ALL
SELECT 'Movie genres count:', COUNT(*) FROM movie_genres;

-- 验证外键关系
SELECT 'Foreign key relationships verification:' AS status;
SELECT
    b.id AS booking_id,
    u.username,
    m.title AS movie_title,
    s.start_time,
    b.seat_numbers,
    b.status
FROM bookings b
JOIN users u ON b.user_id = u.id
JOIN showtimes s ON b.showtime_id = s.id
JOIN movies m ON s.movie_id = m.id
ORDER BY b.id;

-- ===========================================
-- 完成提示
-- ===========================================

SELECT '🎉 Database initialization completed successfully!' AS message;
SELECT '📊 Summary:' AS info;
SELECT
    CONCAT('Users: ', COUNT(*)) AS users
FROM users
UNION ALL
SELECT CONCAT('Movies: ', COUNT(*))
FROM movies
UNION ALL
SELECT CONCAT('Showtimes: ', COUNT(*))
FROM showtimes
UNION ALL
SELECT CONCAT('Bookings: ', COUNT(*))
FROM bookings;