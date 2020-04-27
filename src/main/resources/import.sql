insert into users (email,is_moderator,name,password,reg_time,photo) values ("admin@admin.ru","1","admin","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-01 12:12:01", "img/default.c66f8640.jpg");
insert into users (email,is_moderator,name,password,reg_time,photo) values ("user1@user.ru","0","user1","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-02 12:22:22", "img/default.c66f8640.jpg");
insert into users (email,is_moderator,name,password,reg_time,photo) values ("user2@user.ru","0","user2","$2a$10$y2gw7ewTIv1P82cooxBpIeX6jdiZs2dPUPtOO4kZb0cnpkPK7TM9u","2020-04-03 12:23:23","img/default.c66f8640.jpg");

insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("1","1","ACCEPTED","1","post of admin","2020-04-01 22:23:01","post of admin","10","1");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("2","1","ACCEPTED","1","post of user","2020-04-02 22:23:01","post of user","20","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("3","1","ACCEPTED","1","post of user2","2020-04-03 22:23:01","post of user2","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("4","1","NEW","1","post of user NEW","2020-04-03 22:23:01","post of user new","0","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("5","1","DECLINED","1","post of user DECLINED","2020-04-03 22:23:01","post of user declined","0","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("6","0","NEW","1","non active post user","2020-04-10 12:03:11","non active post user","0","2");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("7","1","ACCEPTED","1","post of user2","2020-04-10 01:23:01","post 7","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("8","1","ACCEPTED","1","post of user2","2020-04-11 02:23:01","post 8","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("9","1","ACCEPTED","1","post of user2","2020-04-12 03:23:01","post 9","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("10","1","ACCEPTED","1","post of user2","2020-04-13 04:23:01","post 10","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("11","1","ACCEPTED","1","post of user2","2020-04-14 05:23:01","post 11","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("12","1","ACCEPTED","1","post of user2","2020-04-15 06:23:01","post 12","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("13","1","ACCEPTED","1","post of user2","2020-04-16 07:23:01","post 13","30","3");
insert into posts (id,is_active,moderation_status,moderator_id,text,time,title,view_count,user_id) values ("14","1","ACCEPTED","1","post of user2","2020-04-16 07:23:01","post 14","30","3");

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

insert into global_settings (code,name,value) values ("MULTIUSER_MODE", "Многопользовательский режим", "y");
insert into global_settings (code,name,value) values ("POST_PREMODERATION", "Премодерация постов", "y");
insert into global_settings (code,name,value) values ("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "y");

