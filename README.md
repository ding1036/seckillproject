### environment
* Idea
* spring boot2.0
* redis 
* fastjson
* thymeleaf
* mysql
* druid

### database structure

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
)ENGINE=InnoDB AUTO_INCREMENT = 3 DEAFULT CHARSET = utf8b4;



CREATE TABLE `seckill_goods` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'seckill good table',
`goods_id` bigint(20) DEFAULT NULL COMMENT 'good id',
`seckill_price` decimal(10,2) DEFAULT '0.00' COMMENT 'seckill price',
`stock_count` int(11) DEFAULT NULL COMMENT 'stock count',
`start_date` datetime DEFAULT NULL COMMENT 'seckill start time',
`end_date` datetime DEFAULT NULL COMMENT 'seckill end time',
PRIMARY KEY(`id`)
)ENGINE = InnoDB AUTO_INCREMENT = 3 DEAFULT CHARSET = utf8b4;

insert into seckill_goods` values (1,1,0.01,4,'2018-3-12 15:00:00','2018-3-12 15:01:00'),(2,2,0.01,9,'2018-3-12 15:00:00','2018-3-12 15:01:00')





```