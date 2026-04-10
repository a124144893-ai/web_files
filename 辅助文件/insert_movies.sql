SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

SET FOREIGN_KEY_CHECKS=0;
DELETE FROM movies WHERE id > 0;
SET FOREIGN_KEY_CHECKS=1;

INSERT INTO movies (title, description, poster_url, director, cast, duration_minutes, rating, release_date, is_hot, created_at, updated_at) VALUES 
('沙丘2', '史诗级科幻大作，展现宇宙政治与人性抉择的碰撞', 'https://example.com/dune2.jpg', '丹尼斯·维伦纽瓦', '蒂莫西·查拉梅,赞达亚', 166, 8.5, '2024-12-20', 1, NOW(), NOW()),
('流浪地球2', '人类为生存而进行的终极逃亡之旅', 'https://example.com/wandering_earth2.jpg', '张艺谋', '吴京,李雪健', 173, 8.2, '2024-11-15', 1, NOW(), NOW()),
('奥本海默', '揭秘原子弹之父的人生故事与伦理困境', 'https://example.com/oppenheimer.jpg', '克里斯托弗·诺兰', '基里安·墨菲,小罗伯特·唐尼', 180, 8.8, '2024-10-01', 1, NOW(), NOW()),
('乐园', '平凡生活中的不凡故事', 'https://example.com/paradise.jpg', '葛优', '葛优,宋丹丹', 112, 7.6, '2024-09-28', 0, NOW(), NOW()),
('深海', '史诗级动画冒险，探索神秘海底世界', 'https://example.com/deep_sea.jpg', '田晓鹏', '配音演员', 128, 7.9, '2024-08-15', 0, NOW(), NOW());

SELECT id, title, director FROM movies;
