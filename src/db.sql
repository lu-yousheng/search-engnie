-- 创建数据库
drop database if exists search;
create database search DEFAULT CHARACTER SET utf8mb4;
-- 使⽤数据数据
use search;
-- 创建表[⽤户表]
drop table if exists userinfo;
create table userinfo(
 id int primary key auto_increment unique,
 username varchar(100) not null,
 password varchar(65) not null,
 nike varchar(10),
 photo varchar(500) default 'img/init.jpg',
 createtime datetime default now(),
 updatetime datetime default now(),
 docount int default 0,
 userRole varchar(10),
 contact varchar(20)
) default charset 'utf8mb4';

-- 创建文档信息表
drop table if exists docinfo;
create table docinfo(
 id int primary key auto_increment unique,
 docname varchar(100) not null,
 docuser varchar(10),
 docgroup int default 0,
 createtime datetime default now(),
 updatetime datetime default now(),
 docexplain varchar(50)
 docpath varchar(50)
) default charset 'utf8mb4';

-- 创建群组信息表
drop table if exists groups;
create table groups(
 group_id int primary key auto_increment unique,
 group_name varchar(20) not null,
 userId int,
 user_name varchar(10),
 createtime datetime default now(),
 description varchar(50),
 FOREIGN KEY (userId) REFERENCES userinfo(id)
) default charset 'utf8mb4';

-- 用户-群组关联表
drop table if exists User_Groups;
create table User_Groups(
 id int primary key auto_increment unique,
 user_id int,
 group_id int,
 role varchar(10),
 joined_at datetime default now(),
 FOREIGN KEY (user_id) REFERENCES userinfo(id),
 FOREIGN KEY (group_id) REFERENCES groups(group_id)
) default charset 'utf8mb4';

-- 群组-文件关联表
drop table if exists Group_Files;
create table Group_Files(
 id int primary key auto_increment unique,
 group_id int,
 doc_id int,
 access varchar(10),
 shared_at datetime default now(),
 FOREIGN KEY (group_id) REFERENCES groups(group_id),
 FOREIGN KEY (doc_id) REFERENCES docinfo(id)
) default charset 'utf8mb4';

-- 创建用户名称的索引
CREATE UNIQUE INDEX user_name ON userinfo(username);