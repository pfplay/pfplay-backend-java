create table user
(
    id             bigint unsigned auto_increment
        primary key,
    email          varchar(255) not null,
    nickname       varchar(255) null,
    introduction   varchar(255) null,
    authority      varchar(255) not null,
    wallet_address varchar(255) null,
    create_time    datetime(6) null,
    dj_score       int default 0 null,
    task_score     int default 0 null,
    body_id        int unsigned default '1' null,
    face_url       varchar(500) null,
    constraint user_email
        unique (email)
) engine=InnoDB;

alter table user
    add constraint unique_user_email unique (email)
;