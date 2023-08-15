create table party_room (
    id integer unsigned not null auto_increment,
    created_at datetime default current_timestamp,
    djing_limit integer comment '디제잉 시간',
    domain varchar(255),
    introduce varchar(255),
    name varchar(255),
    status varchar(50) comment '파티룸 활성화 여부',
    type varchar(50) comment '파티룸 타입',
    updated_at datetime(6),
    user_id integer UNSIGNED,
    primary key (id)
) engine=InnoDB;

alter table party_room
    add constraint unique_party_room_name unique (name)
;

alter table party_room
    add constraint fk_party_room_user_id
    foreign key (user_id)
    references user (id)
;