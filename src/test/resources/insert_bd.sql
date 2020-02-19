
--INSERT users
INSERT INTO `blog_site`.`users`(`id`,`email`,`is_moderator`,`name`,`password`,`reg_time`)
VALUES
('1','admin@admin.ru','1','admin','admin','2020-01-01 12:12:01'),
('2','user1@user1.ru','0','user1','user1','2020-01-02 22:22:22'),
('3','user2@user2.ru','0','user2','user2','2020-01-03 23:23:23');

--INSERT posts
INSERT INTO `blog_site`.`posts`
(`id`,`is_active`,`moderation_status`,`moderator_id`,`text`,`time`,`title`,`view_count`,`user_id`)
VALUES
('1','1','ACCEPTED','1','post of admin','2020-01-01 22:23:01','post of admin','10','1'),
('2','1','ACCEPTED','1','post of user','2020-01-22 22:23:01','post of user','20','2'),
('3','1','ACCEPTED','1','post of user2','2020-03-23 22:23:01','post of user2','30','3');

--INSERT post_votes
INSERT INTO `blog_site`.`post_votes`
(`id`,`time`,`value`,`post_id`,`user_id`)
VALUES
('1','1.2.2020','1','1','1'),
('2','1.2.2020','1','1','2'),
('3','1.2.2020','1','1','3'),
('4','1.2.2020','1','2','1'),
('5','1.2.2020','1','2','2'),
('6','1.2.2020','1','3','1');

--INSERT comments
INSERT INTO `blog_site`.`post_comments`
(`id`,`comment`,`time`,`post_id`,`user_id`)
VALUES
('1','comment admin','2020-01-24 22:23:01','1','2'),
('2','comment admin','2020-01-24 22:23:01','1','2'),
('3','comment admin','2020-01-24 22:23:01','1','2'),
('4','comment user1','2020-01-24 22:23:01','2','1');

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
('3','3','3');
