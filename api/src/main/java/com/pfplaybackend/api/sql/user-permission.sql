create table user_permission (
     id integer unsigned not null auto_increment,
     authority varchar(255),
     chat integer comment '채팅',
     create_party_room integer comment '파티룸 생성',
     create_play_list integer comment '플레이리스트 생성',
     create_wait_dj integer comment 'DJ 대기열 등록',
     enter_main_stage integer comment '메인 스테이지 입장',
     enter_party_room integer comment '파티룸 입장',
     setting_profile integer comment '프로필 설정',
     show_party_list_display integer comment '파티목록 화면',
     admin integer comment '계급 권한 관리자',
     community_manager integer comment '계급 권한',
     clubber integer comment '계급 권한',
     listener integer comment '계급 권한',
     moderator integer comment '계급 권한',
     primary key (id)
) comment='기능 접근 권한' engine=InnoDB;

create index user_permission_authority on user_permission (authority)
;