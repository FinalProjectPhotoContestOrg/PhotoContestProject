create table users
(
    id            int auto_increment
        primary key,
    username      varchar(50)                                                                                           not null,
    first_name    varchar(50)                                                                                           not null,
    last_name     varchar(50)                                                                                           not null,
    email         varchar(70)                                                                                           not null,
    password_hash varchar(255)                                                                                          not null,
    role          enum ('Organizer', 'Junkie')                                                                          not null,
    ranking       enum ('Junkie', 'Enthusiast', 'Master', 'WiseAndBenevolentPhotoDictator') default 'Junkie'            null,
    points        int                                                                       default 0                   null,
    created_at    timestamp                                                                 default current_timestamp() null,
    constraint username
        unique (username)
);

create table contests
(
    id              int auto_increment
        primary key,
    title           varchar(255)                                                       not null,
    category        varchar(100)                                                       not null,
    type            enum ('Open', 'Invitational')                                      not null,
    phase           enum ('PhaseI', 'PhaseII', 'Finished') default 'PhaseI'            null,
    phase_1_end     timestamp                                                          not null,
    phase_2_end     timestamp                                                          not null,
    organizer_id    int                                                                not null,
    cover_photo_url mediumtext                                                         null,
    created_at      timestamp                              default current_timestamp() null,
    constraint title
        unique (title),
    constraint contests_ibfk_1
        foreign key (organizer_id) references users (id)
);

create table contest_jurors
(
    contest_id int not null,
    juror_id   int not null,
    primary key (contest_id, juror_id),
    constraint contest_jurors_ibfk_1
        foreign key (contest_id) references contests (id),
    constraint contest_jurors_ibfk_2
        foreign key (juror_id) references users (id)
);

create index juror_id
    on contest_jurors (juror_id);

create table contest_participants
(
    contest_id int not null,
    user_id    int not null,
    constraint contest_participants_contests_id_fk
        foreign key (contest_id) references contests (id)
            on update cascade on delete cascade,
    constraint contest_participants_users_id_fk
        foreign key (user_id) references users (id)
            on update cascade on delete cascade
);

create index idx_contest_category
    on contests (category);

create index idx_contest_title
    on contests (title);

create index organizer_id
    on contests (organizer_id);

create table entries
(
    id              int auto_increment
        primary key,
    title           varchar(255)                          not null,
    story           text                                  not null,
    photo_url       mediumtext                            not null,
    participant_id  int                                   not null,
    contest_id      int                                   not null,
    uploaded_at     timestamp default current_timestamp() null,
    entryTotalScore int       default 0                   null,
    constraint entries_ibfk_1
        foreign key (participant_id) references users (id),
    constraint entries_ibfk_2
        foreign key (contest_id) references contests (id)
);

create index contest_id
    on entries (contest_id);

create index idx_photo_title
    on entries (title);

create index participant_id
    on entries (participant_id);

create table ratings
(
    id                int auto_increment
        primary key,
    entry_id          int                                    not null,
    juror_id          int                                    not null,
    score             int                                    null
        check (`score` between 1 and 10 or `score` = 0 and `category_mismatch` = 1),
    comment           text                                   not null,
    category_mismatch tinyint(1) default 0                   null,
    reviewed_at       timestamp  default current_timestamp() null,
    constraint ratings_ibfk_1
        foreign key (entry_id) references entries (id),
    constraint ratings_ibfk_2
        foreign key (juror_id) references users (id)
);

create index juror_id
    on ratings (juror_id);

create index photo_id
    on ratings (entry_id);

create index idx_username
    on users (username);

