--DROP DATABASE `blog_site`;
--CREATE DATABASE `blog_site`;
--USE  `blog_site`;
--
--CREATE TABLE `users` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `code` varchar(255) DEFAULT NULL,
--  `email` varchar(255) NOT NULL,
--  `is_moderator` tinyint NOT NULL,
--  `name` varchar(255) NOT NULL,
--  `password` varchar(255) NOT NULL,
--  `photo` text,
--  `reg_time` datetime NOT NULL,
--PRIMARY KEY (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `posts` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `is_active` tinyint NOT NULL,
--  `moderation_status` enum('NEW','ACCEPTED','DECLINED') NOT NULL DEFAULT 'NEW',
--`moderator_id` int DEFAULT NULL,
--`text` text NOT NULL,
--`time` datetime NOT NULL,
--`title` varchar(255) NOT NULL,
--`view_count` int NOT NULL,
--`user_id` int NOT NULL,
--PRIMARY KEY (`id`),
--KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
--CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `post_votes` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `post_id` int NOT NULL,
--  `time` datetime NOT NULL,
--  `user_id` int NOT NULL,
--  `value` tinyint NOT NULL,
--  PRIMARY KEY (`id`),
--KEY `FK9q09ho9p8fmo6rcysnci8rocc` (`user_id`),
--KEY `FK9jh5u17tmu1g7xnlxa77ilo3u` (`post_id`),
--CONSTRAINT `FK9jh5u17tmu1g7xnlxa77ilo3u` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
--CONSTRAINT `FK9q09ho9p8fmo6rcysnci8rocc` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `tags` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `name` varchar(255) NOT NULL,
--PRIMARY KEY (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `tag2post` (
--  `id` int NOT NULL,
--  `post_id` int NOT NULL,
--  `tag_id` int NOT NULL,
--  PRIMARY KEY (`post_id`,`tag_id`),
--KEY `FKjou6suf2w810t2u3l96uasw3r` (`tag_id`),
--CONSTRAINT `FKjou6suf2w810t2u3l96uasw3r` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`),
--CONSTRAINT `FKpjoedhh4h917xf25el3odq20i` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `post_comments` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `parent_id` int DEFAULT NULL,
--  `comment` text NOT NULL,
--  `time` datetime NOT NULL,
--  `post_id` int NOT NULL,
--  `user_id` int NOT NULL,
--  PRIMARY KEY (`id`),
--KEY `FKaawaqxjs3br8dw5v90w7uu514` (`post_id`),
--KEY `FKsnxoecngu89u3fh4wdrgf0f2g` (`user_id`),
--CONSTRAINT `FKaawaqxjs3br8dw5v90w7uu514` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
--CONSTRAINT `FKsnxoecngu89u3fh4wdrgf0f2g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `global_settings` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `code` varchar(255) NOT NULL,
--  `name` varchar(255) NOT NULL,
--  `value` char(1) NOT NULL,
--PRIMARY KEY (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
--CREATE TABLE `captcha_codes` (
--  `id` int NOT NULL AUTO_INCREMENT,
--  `code` tinytext NOT NULL,
--  `secret_code` tinytext NOT NULL,
--  `time` datetime NOT NULL,
--PRIMARY KEY (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users`(`email`,`is_moderator`,`name`,`password`,`reg_time`,`photo`)
VALUES
('admin@admin.ru','1','admin','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-01 12:12:01', 'img/default.c66f8640.jpg'),
('user1@user.ru','0','user1','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-02 12:22:22', 'img/default.c66f8640.jpg'),
('user2@user.ru','0','user2','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-03 12:23:23','img/default.c66f8640.jpg');

INSERT INTO `posts`
(`id`,`is_active`,`moderation_status`,`moderator_id`,`text`,`time`,`title`,`view_count`,`user_id`)
VALUES
('1','1','ACCEPTED','1','post of admin','2020-04-01 22:23:01','post of admin','10','1'),
('2','1','ACCEPTED','1','post of user','2020-04-02 22:23:01','post of user','20','2'),
('3','1','ACCEPTED','1','post of user2','2020-04-03 22:23:01','post of user2','30','3'),
('4','1','NEW','1','post of user NEW','2020-04-03 22:23:01','post of user new','0','2'),
('5','1','DECLINED','1','post of user DECLINED','2020-04-03 22:23:01','post of user declined','0','2'),
('6','0','NEW','1','non active post user','2020-04-10 12:03:11','non active post user','0','2'),
('7','1','ACCEPTED','1','post of user2','2020-04-10 01:23:01','post 7','30','3'),
('8','1','ACCEPTED','1','post of user2','2020-04-11 02:23:01','post 8','30','3'),
('9','1','ACCEPTED','1','post of user2','2020-04-12 03:23:01','post 9','30','3'),
('10','1','ACCEPTED','1','post of user2','2020-04-13 04:23:01','post 10','30','3'),
('11','1','ACCEPTED','1','post of user2','2020-04-14 05:23:01','post 11','30','3'),
('12','1','ACCEPTED','1','post of user2','2020-04-15 06:23:01','post 12','30','3'),
('13','1','ACCEPTED','1','post of user2','2020-04-16 07:23:01','post 13','30','3'),
('14','1','ACCEPTED','1','post of user2','2020-04-16 07:23:01','post 14','30','3');



INSERT INTO `post_votes`
(`id`,`time`,`value`,`post_id`,`user_id`)
VALUES
('1','2020-01-24 22:23:01','1','1','1'),
('2','2020-01-24 22:23:01','1','1','2'),
('3','2020-01-24 22:23:01','1','1','3'),
('4','2020-01-24 22:23:01','1','2','1'),
('5','2020-01-24 22:23:01','1','2','2'),
('6','2020-01-24 22:23:01','1','3','1');


INSERT INTO `post_comments`
(`id`,`comment`,`time`,`post_id`,`user_id`)
VALUES
('1','comment user1 to admin 1','2020-04-10 22:23:01','1','2'),
('2','comment user1 to admin 2','2020-04-11 22:24:01','1','2'),
('3','comment user1 to admin 2','2020-01-24 22:25:01','1','2'),
('4','comment admin to user1 ','2020-01-24 22:25:01','2','1');


INSERT INTO `tags`
(`id`,`name`)
VALUES
('1','java'),
('2','python'),
('3','kotlin');

INSERT INTO `tag2post`
(`id`,`post_id`,`tag_id`)
VALUES
('1','1','1'),
('2','2','2'),
('3','3','3'),
('4','1','2');


INSERT INTO `global_settings` ( `code`, `name`, `value`) VALUES
('MULTIUSER_MODE', 'Многопользовательский режим', 'y'),
('POST_PREMODERATION','Премодерация постов','y'),
('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога','y');

INSERT INTO `captcha_codes` ( `code`, `secret_code`, `time`) VALUES
('12u3h', '-23693860', '2020-04-18 11:08:22');



