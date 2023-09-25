create table party_room_ban (
    id bigint unsigned not null auto_increment,
    user_id bigint unsigned,
    party_room_id bigint unsigned,
    ban integer default 0,
    chat integer default 0 comment '30초 채팅금지',
    kick integer default 0,
    authority varchar(255) comment '유저 롤 타입',
    reason varchar(255) comment '밴,킥 사유',
    created_at datetime default current_timestamp not null,
    updated_at datetime default current_timestamp on update current_timestamp not null,
    primary key (id)
) comment='밴 유저 목록' engine=InnoDB
;

create index idx_party_room_ban_01
    on party_room_ban (user_id, party_room_id, authority)
;