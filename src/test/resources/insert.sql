insert into users (email,is_moderator,name,password,reg_time,photo) values ("admin@admin.ru","1","admin","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-01 12:12:01", "img/default.c66f8640.jpg");
insert into users (email,is_moderator,name,password,reg_time,photo) values ("user1@user.ru","0","user1","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-02 12:22:22", "img/default.c66f8640.jpg");
insert into users (email,is_moderator,name,password,reg_time,photo) values ("user2@user.ru","0","user2","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-03 12:23:23","img/default.c66f8640.jpg");


insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("1","1","ACCEPTED","1","post of admin","2020-04-01 22:23:01","post of admin","10","1");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("2","1","ACCEPTED","1","post of user","2020-04-02 22:23:01","post of user","20","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("3","1","ACCEPTED","1","post of user2","2020-04-03 22:23:01","post of user2","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("4","1","NEW","1","post of user NEW","2020-04-03 22:23:01","post of user new","0","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("5","1","DECLINED","1","post of user DECLINED","2020-04-03 22:23:01","post of user declined","0","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("6","0","NEW","1","non active post user","2020-04-10 12:03:11","non active post user","0","2");


insert into post_votes (id,time,value,post_id,user_id) values ("1","2020-01-24 22:23:01","1","1","1");
insert into post_votes (id,time,value,post_id,user_id) values ("2","2020-01-24 22:23:01","1","1","2");
insert into post_votes (id,time,value,post_id,user_id) values ("3","2020-01-24 22:23:01","1","1","3");
insert into post_votes (id,time,value,post_id,user_id) values ("4","2020-01-24 22:23:01","1","2","1");
insert into post_votes (id,time,value,post_id,user_id) values ("5","2020-01-24 22:23:01","1","2","2");
insert into post_votes (id,time,value,post_id,user_id) values ("6","2020-01-24 22:23:01","1","3","1");


insert into post_comments (id,comment,time,post_id,user_id) values ("1","comment user1 to admin 1","2020-04-10 22:23:01","1","2");
insert into post_comments (id,comment,time,post_id,user_id) values ("2","comment user1 to admin 2","2020-04-11 22:24:01","1","2");
insert into post_comments (id,comment,time,post_id,user_id) values ("3","comment user1 to admin 2","2020-01-24 22:25:01","1","2");
insert into post_comments (id,comment,time,post_id,user_id) values ("4","comment admin to user1 ","2020-01-24 22:25:01","2","1");

insert into tags (id,name) values ("1","java");
insert into tags (id,name) values ("2","python");
insert into tags (id,name) values ("3","kotlin");

insert into tag2post (id,post_id,tag_id) values ("1","1","1");
insert into tag2post (id,post_id,tag_id) values ("2","2","2");
insert into tag2post (id,post_id,tag_id) values ("3","3","3");
insert into tag2post (id,post_id,tag_id) values ("4","1","2");

INSERT INTO captcha_codes (code,secret_code,time) VALUES ('12u3h', '-23693860', '2020-04-18 11:08:22');

insert into global_settings (code,name,value) values ("MULTIUSER_MODE", "Многопользовательский режим", "y");
insert into global_settings (code,name,value) values ("POST_PREMODERATION", "Премодерация постов", "y");
insert into global_settings (code,name,value) values ("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "y");


--INSERT INTO `blog_site_test`.`users`(`email`,`is_moderator`,`name`,`password`,`reg_time`,`photo` )
--VALUES
--('admin@admin.ru','1','admin','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-01 12:12:01', 'img/default.c66f8640.jpg'),
--('user1@user.ru','0','user1','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-02 12:22:22', 'img/default.c66f8640.jpg'),
--('user2@user.ru','0','user2','$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u','2020-04-03 12:23:23','img/default.c66f8640.jpg');
--
--
--INSERT INTO `blog_site_test`.`posts`
--(`id`,`is_active`,`moderation_status`,`moderator_id`,`text`,`time`,`title`,`view_count`,`user_id`)
--VALUES
--('1','1','ACCEPTED','1','post of admin','2020-04-01 22:23:01','post of admin','10','1'),
--('2','1','ACCEPTED','1','post of user','2020-04-02 22:23:01','post of user','20','2'),
--('3','1','ACCEPTED','1','post of user2','2020-04-03 22:23:01','post of user2','30','3'),
--('4','1','NEW','0','post of user NEW','2020-04-03 22:23:01','post of user new','0','2'),
--('5','1','DECLINED','1','post of user DECLINED','2020-04-03 22:23:01','post of user declined','0','2'),
--('6','0','NEW','0','non active post user','2020-04-10 12:03:11','non active post user','0','2');
--
--
--
----INSERT post_votes
--INSERT INTO `blog_site_test`.`post_votes`
--(`id`,`time`,`value`,`post_id`,`user_id`)
--VALUES
--('1','2020-01-24 22:23:01','1','1','1'),
--('2','2020-01-24 22:23:01','1','1','2'),
--('3','2020-01-24 22:23:01','1','1','3'),
--('4','2020-01-24 22:23:01','1','2','2'),
--('5','2020-01-24 22:23:01','1','2','2'),
--('6','2020-01-24 22:23:01','1','3','2');
--
----INSERT comments
--INSERT INTO `blog_site_test`.`post_comments`
--(`id`,`comment`,`time`,`post_id`,`user_id`)
--VALUES
--('1','comment user1 to admin 1','2020-04-10 22:23:01','1','2'),
--('2','comment user1 to admin 2','2020-04-11 22:24:01','1','2'),
--('3','comment user1 to admin 2','2020-01-24 22:25:01','1','2'),
--('4','comment admin to user1 ','2020-01-24 22:25:01','2','1');
--
----INSERT tags
--INSERT INTO `blog_site_test`.`tags`
--(`id`,`name`)
--VALUES
--('1','java'),
--('2','python'),
--('3','kotlin');
--
----INSERT tags2post
--INSERT INTO `blog_site_test`.`tag2post`
--(`id`,`post_id`,`tag_id`)
--VALUES
--('1','1','1'),
--('2','2','2'),
--('3','3','3'),
--('4','1','2');
--
--
--INSERT INTO `blog_site_test`.`global_settings` ( `code`, `name`, `value`) VALUES
--('MULTIUSER_MODE', 'Многопользовательский режим', 'y'),
--('POST_PREMODERATION','Премодерация постов','y'),
--('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога','y');
--
--INSERT INTO `blog_site_test`.`captcha_codes` ( `code`, `secret_code`, `time`) VALUES
--('12u3h', '-23693860', '2020-04-18 11:08:22');
--
--
--
