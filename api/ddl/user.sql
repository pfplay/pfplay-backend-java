# 임시 쿼리
create table user (
  id integer primary key ,
  email varchar(255) not null unique,
  access_token varchar(255),
  authority varchar(255),
  create_time datetime(6),
  dj_score varchar(255),
  introduction varchar(255),
  nickname varchar(255),
  refresh_token varchar(255),
  task_score varchar(255),
  wallet_address varchar(255)
) engine=InnoDB