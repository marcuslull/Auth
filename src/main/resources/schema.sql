-- default schema for spring security Users

drop sequence if exists users_SEQ;
drop index if exists ix_auth_username;
drop table if exists authorities;
drop table if exists users;


create table users(
                      id bigint not null primary key,
                      username varchar(50) not null,
                      password varchar(500) not null,
                      enabled boolean not null
);

create table authorities (
                             id bigint not null,
                             authority bytea not null,
                             constraint fk_authorities_users foreign key(id) references users
);

create unique index ix_auth_username on authorities (id,authority);

create sequence users_SEQ start with 100 increment by 1;