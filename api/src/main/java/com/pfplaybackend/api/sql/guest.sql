CREATE TABLE `guest` (
     `id` integer UNSIGNED not null auto_increment,
     `name` varchar(50) not null,
     `kick` char(2) not null default 0,
     `ban` char(2) not null default 0,
     `agent` varchar(255) not null comment 'USER-AGENT',
     `reason` varchar(255) comment 'kick,ban 사유',
     `created_at` datetime ,
     `updated_at` datetime ,

     primary key user_id(id)
) engine=InnoDB
;