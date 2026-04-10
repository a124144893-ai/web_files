-- admin密码: password (BCrypt)
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7Hq3JQHvK0K2' WHERE username = 'admin';

-- user1-4密码: 123456 (BCrypt)
UPDATE users SET password = '$2a$12$tXEd.UlvKVv8tBLXl1PwY.Dq2w3Cxuw0eMDEWrKGSFTn9kQ7PH1py' WHERE username IN ('user1', 'user2', 'user3', 'user4');

SELECT username, password FROM users ORDER BY id;
