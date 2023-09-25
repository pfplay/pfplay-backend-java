create table party_room (
    id bigint unsigned not null auto_increment,
    user_id bigint unsigned,
    domain varchar(255),
    introduce varchar(255),
    name varchar(255),
    djing_limit integer comment '디제잉 시간',
    status varchar(50) comment '파티룸 활성화 여부',
    type varchar(50) comment '파티룸 타입',
    created_at datetime default current_timestamp not null,
    updated_at datetime default current_timestamp on update current_timestamp not null,
    primary key (id)
) engine=InnoDB;

alter table party_room
    add constraint unique_party_room_name unique (name)
;

alter table party_room
    add constraint unique_party_room_domain unique (domain)
;

create index idx_party_room_user_id
    on party_room (user_id)
;

create index idx_party_room_01
    on party_room (domain, introduce, name, status, type)
;