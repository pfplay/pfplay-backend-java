create table guest (
   id bigint unsigned not null auto_increment,
   agent varchar(255),
   created_at datetime default current_timestamp not null,
   name varchar(50) not null,
   primary key (id)
) engine=InnoDB