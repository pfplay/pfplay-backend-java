create table guest
(
    id         bigint unsigned auto_increment
        primary key,
    agent      varchar(255)                       null,
    created_at datetime default CURRENT_TIMESTAMP not null,
    name       varchar(50)                        not null,
    updated_at datetime                           not null on update CURRENT_TIMESTAMP
);