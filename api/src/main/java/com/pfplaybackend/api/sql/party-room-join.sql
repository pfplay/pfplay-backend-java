create table party_room_join (
     id bigint unsigned not null,
     room_id bigint unsigned,
     ban_id bigint unsigned,
     user_id bigint unsigned,
     party_permission_role varchar(255) comment '파티 권한',
     active varchar(255) comment '파티 활성화 여부',
     created_at datetime default current_timestamp not null,
     updated_at datetime default current_timestamp on update current_timestamp not null,
     primary key (id)
) comment='파티룸 접속 테이블' engine=InnoDB;

create index idx_party_room_join_01 on party_room_join (room_id, user_id);