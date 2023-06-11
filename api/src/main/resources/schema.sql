CREATE TABLE IF NOT EXISTS `user` (
  id integer UNSIGNED not null auto_increment,
  email varchar(255) not null,
  nickname varchar(255),
  introduction varchar(255),
  authority varchar(255) not null,
  wallet_address varchar(255),
  create_time datetime(6),
  dj_score integer default 0,
  task_score integer default 0,

  primary key user_id(id),
  unique key user_email(email)
) engine=InnoDB
;