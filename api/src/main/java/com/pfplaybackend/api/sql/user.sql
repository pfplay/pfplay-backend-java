create table user
(
    id             int unsigned auto_increment
        primary key,
    email          varchar(255)                                                                                                                                                                                                                                         not null,
    nickname       varchar(255)                                                                                                                                                                                                                                         null,
    introduction   varchar(255)                                                                                                                                                                                                                                         null,
    authority      varchar(255)                                                                                                                                                                                                                                         not null,
    wallet_address varchar(255)                                                                                                                                                                                                                                         null,
    created_at     datetime(6)                                                                                                                                                                                                                                          null,
    dj_score       int          default 0                                                                                                                                                                                                                               null,
    task_score     int          default 0                                                                                                                                                                                                                               null,
    body_id        int unsigned default '1'                                                                                                                                                                                                                             null,
    face_url       varchar(500) default 'https://postfiles.pstatic.net/MjAyMzExMDVfMTE0/MDAxNjk5MTc4Nzc1MDg2.GxFLnHCjN1Eny-pPpYj3I0rZy_0Zz0Sk00Hu1QO53ukg.k-FUmlNd9AZcIAFKFiXlOppjmZdo9EzJsjiX7Ml9V8Ag.PNG.sylviuss/Screen_Shot_2023-11-05_at_7.05.40_PM.png?type=w773' null,
    updated_at     datetime     default CURRENT_TIMESTAMP                                                                                                                                                                                                               not null on update CURRENT_TIMESTAMP,
    constraint user_email
        unique (email)
);