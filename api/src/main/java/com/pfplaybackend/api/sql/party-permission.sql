create table party_permission (
  id integer unsigned not null auto_increment,
  authority varchar(255),
  party_info_fetch integer comment '파티 정보 수정',
  party_close integer comment '파티룸 폐쇄',
  notice integer comment '공지 작성/삭제/수정',
  give_to_clubber integer comment '클러버 권한 부여',
  chat_delete integer comment '채팅 삭제',
  chat_limit_ban_to_clubber integer comment '클러버 패널티 30초 채팅 금지',
  ban_to_clubber integer comment '클러버 패널티 30초 킥(재입장 불가능)',
  kick_to_clubber integer comment '클러버 패널티 30초 킥(재입장 가능)',
  dj_wait_lock integer comment 'DJ 대기열 잠금',
  chat_ban integer comment '채팅 차단',
  new_dj integer comment '신규 DJ 추가/삭제',
  music_skip integer comment '음악 스킵',
  video_length_limit integer comment '영상 길이 제한',
  primary key (id)
) comment='파티 권한' engine=InnoDB
;

create index party_permission_authority on party_permission (authority)
;
