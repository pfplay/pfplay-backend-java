create table guest
(
    id         int unsigned auto_increment
        primary key,
    name       varchar(50)                          not null,
    agent      varchar(255)                         null,
    ban        varchar(2) default '0'               not null,
    kick       varchar(2) default '0'               not null,
    reason     varchar(255)                         null,
    created_at datetime   default CURRENT_TIMESTAMP not null,
    updated_at datetime(6)                          null
);

