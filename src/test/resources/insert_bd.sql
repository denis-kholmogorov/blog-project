
--INSERT users
INSERT INTO `blog_site`.`users`(`id`,`email`,`is_moderator`,`name`,`password`,`reg_time`)
VALUES
('1','admin@admin.ru','1','admin','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-01 12:12:01'),
('2','user1@user.ru','0','user1','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-02 12:22:22'),
('3','user2@user.ru','0','user2','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-03 12:23:23');

--INSERT posts
INSERT INTO `blog_site`.`posts`
(`id`,`is_active`,`moderation_status`,`moderator_id`,`text`,`time`,`title`,`view_count`,`user_id`)
VALUES
('1','1','ACCEPTED','1','post of admin','2020-04-01 22:23:01','post of admin','10','1'),
('2','1','ACCEPTED','1','post of user','2020-04-02 22:23:01','post of user','20','2'),
('3','1','ACCEPTED','1','post of user2','2020-04-03 22:23:01','post of user2','30','3'),
('4','1','NEW','1','post of user2 NEW','2020-04-03 22:23:01','post of user2','0','3'),
('5','1','NEW','1','post of user2 NEW','2020-04-03 22:23:01','post of user2','0','3');



--INSERT post_votes
INSERT INTO `blog_site`.`post_votes`
(`id`,`time`,`value`,`post_id`,`user_id`)
VALUES
('1','2020-01-24 22:23:01','1','1','1'),
('2','2020-01-24 22:23:01','1','1','2'),
('3','2020-01-24 22:23:01','1','1','3'),
('4','2020-01-24 22:23:01','1','2','1'),
('5','2020-01-24 22:23:01','1','2','2'),
('6','2020-01-24 22:23:01','1','3','1');

--INSERT comments
INSERT INTO `blog_site`.`post_comments`
(`id`,`comment`,`time`,`post_id`,`user_id`)
VALUES
('1','comment user1 to admin 1','2020-04-10 22:23:01','1','2'),
('2','comment user1 to admin 2','2020-04-11 22:24:01','1','2'),
('3','comment user1 to admin 2','2020-01-24 22:25:01','1','2'),
('4','comment admin to user1 ','2020-01-24 22:25:01','2','1');

--INSERT tags
INSERT INTO `blog_site`.`tags`
(`id`,`name`)
VALUES
('1','java'),
('2','python'),
('3','kotlin');

--INSERT tags2post
INSERT INTO `blog_site`.`tag2post`
(`id`,`post_id`,`tag_id`)
VALUES
('1','1','1'),
('2','2','2'),
('3','3','3'),
('4','1','2'),
('5','1','3');

INSERT INTO `blog_site`.`global_settings` ( `code`, `name`, `value`) VALUES
( 'MULTIUSER_MODE', 'Многопользовательский режим', 'y'),
('POST_PREMODERATION','Премодерация постов','y'),
('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога','y');

INSERT INTO `blog_site`.`captcha_codes` ( `code`, `secret_code`, `time`) VALUES
('12u3h', '-23693860', '2020-04-18 11:08:22');



