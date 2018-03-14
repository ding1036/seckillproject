### environment
* Idea
* spring boot1.5.8
* redis 
* fastjson
* thymeleaf
* mysql
* druid
* RabbitMq
* Erlang

### database structure
#### create database
```
create database seckill;
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,ALTER ON seckill.* TO seckill@localhost IDENTIFIED BY '123456';
SET PASSWORD FOR 'seckill'@'localhost'=OLD_PASSWORD('123456');
```
```
CREATE TABLE IF NOT EXISTS `user`(
   `id` INT NOT NULL,
   `name` VARCHAR(30) NOT NULL,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
#### basic mysql command
```
change mysql password: SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123456');
enter mysql:    mysql -uroot -p123456
enter database:   use databaseName;
show table list:  show tables;
show table structure:  desc tablename;
```

#### create table `user`
```
CREATE TABLE IF NOT EXISTS `user`(
   `id` INT NOT NULL,
   `name` VARCHAR(30) NOT NULL,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
#### create table `seckill_user`
```
CREATE TABLE IF NOT EXISTS `seckill_user`(
   `id` bigint(20) NOT NULL COMMENT 'user id',
   `nickname` VARCHAR(30) NOT NULL,
   `password` varchar(32) DEFAULT NULL COMMENT 'MD5 + salt',
   `salt` varchar(10) DEFAULT NULL,
 `register_date` datetime DEFAULT NULL COMMENT 'register date',
`last_login_date` datetime DEFAULT NULL COMMENT 'last login time',
 `head` varchar(128) DEFAULT NULL COMMENT'head picture,cloud storage id',
 `login_count` int(11) DEFAULT '0' COMMENT 'login time',
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
#### create table `goods`
```
CREATE TABLE `goods`(
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'good id',
`goods_name` varchar(16) DEFAULT NULL COMMENT 'good name',
`goods_title` varchar(64) DEFAULT NULL COMMENT 'good title',
`goods_img` varchar(64) DEFAULT NULL COMMENT 'good img',
`goods_detail` longtext DEFAULT NULL COMMENT 'good detail',
`goods_price` decimal(10,2) DEFAULT '0.00' COMMENT 'good price',
`goods_stock` int(11) DEFAULT NULL COMMENT 'good stock',
PRIMARY KEY(`id`)
)ENGINE=InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8;
```
#### create table `seckill_goods`

```
CREATE TABLE `seckill_goods` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'seckill good table',
`goods_id` bigint(20) DEFAULT NULL COMMENT 'good id',
`seckill_price` decimal(10,2) DEFAULT '0.00' COMMENT 'seckill price',
`stock_count` int(11) DEFAULT NULL COMMENT 'stock count',
`start_date` datetime DEFAULT NULL COMMENT 'seckill start time',
`end_date` datetime DEFAULT NULL COMMENT 'seckill end time',
PRIMARY KEY(`id`)
)ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8;

insert into `seckill_goods` values (1,1,0.01,4,'2018-3-12 15:00:00','2018-3-12 15:01:00'),(2,2,0.01,9,'2018-3-12 15:00:00','2018-3-12 15:01:00');

```
#### create table `order_info`
```
create table `order_info`(
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`user_id` bigint(20) DEFAULT NULL COMMENT 'user id',
`goods_id` bigint(20) DEFAULT NULL COMMENT 'goods id',
`delivery_addr_id` bigint(20) DEFAULT NULL COMMENT 'address id',
`goods_name` varchar(15) DEFAULT NULL COMMENT 'goods name',
`goods_count` int(11) DEFAULT '0' COMMENT 'goods number',
`goods_price` decimal(10,2) DEFAULT '0.00' COMMENT 'goods price',
`order_channel` tinyint(4) DEFAULT '0' COMMENT 'order channel',
`status` tinyint(4) DEFAULT '0' COMMENT '0:new 1:paid 2:send 3.receive 4.return money 5.finished',
`create_date` datetime DEFAULT NULL COMMENT 'create time',
`pay_date` datetime DEFAULT NULL COMMENT 'pay time',
PRIMARY KEY(`id`)
)ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8mb4;
```
#### create table `seckill_order`
```
create table `seckill_order`(
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`user_id` bigint(20) DEFAULT NULL COMMENT 'user id',
`order_id` bigint(20) DEFAULT NULL COMMENT 'order id',
`goods_id` bigint(20) DEFAULT NULL COMMENT 'goods id',
PRIMARY KEY(`id`)
)ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8mb4;
```
#### create unique index to avoid one user request more than once at the same time
```
create unique index u_uid_gid on seckill_order(user_id,goods_id);
alter table `seckill_order` add unique index `u_uid_gid`('user_id','goods_id');
``` 
### test sql
```
update seckill_goods set start_date = '2018-03-13 20:54:00',end_date = '2018-03-13 20:56:00' where id in ('1','2');

```
### presure test
#### redis presure test
*100 thread, 100000 request
```
redis-benchmark -h 127.0.0.1 -p 6379 -c 100 -n 100000
```
*store 100 byte data package
```
redis-benchmark -h 127.0.0.1 -p 6379 -q -d 100
```
*test set and lpush
```
redis-benchmark -t set,lpush -q -n 100000
```
### rabbitmq
#### basic install & operation
install Erlang and rabbitmq: http://blog.csdn.net/a295277302/article/details/71246941
```
start:sudo rabbitmq-server start
shutdown: sudo rabbitmq-server stop
restart: sudo rabbitmq-server restart
check status:sudo rabbitmqctl status
```
#### allow remote connect
cd /etc/rabbitmq    //your rabbitmq root path <br/>
create rabbitmq.config, and add following code
```
[{rabbit, [{loopback_users, []}]}].

```
you can find details in: http://www.rabbitmq.com/access-control.html

