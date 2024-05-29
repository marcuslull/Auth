drop sequence if exists users_SEQ;
drop sequence if exists clients_SEQ;
drop sequence if exists scopes_SEQ;
drop sequence if exists redirects_SEQ;
drop sequence if exists grants_SEQ;
drop sequence if exists auth_method_SEQ;
drop index if exists ix_auth_username;
drop table if exists authorities;
drop table if exists verification;
drop table if exists users;

drop table if exists authorizations;
drop table if exists redirects;
drop table if exists scopes;
drop table if exists grants;
drop table if exists auth_method;
drop table if exists clients;


create table users (
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

create table verification (
    id bigint not null,
    code varchar(50) not null,
    created timestamp not null,
    constraint fk_verification_users foreign key(id) references users
);

create table clients (
    id bigint,
    client_id varchar(100),
    name varchar(50),
    secret varchar(500),
    client_settings bytea,
    token_settings bytea
);

create table authorizations (
    id bigint,
    principal_name varchar(200),
    access_token_type varchar(100),
    access_token_value bytea,
    access_token_issued_at timestamp,
    access_token_expires_at timestamp,
    access_token_scopes varchar(1000),
    refresh_token_value bytea,
    refresh_token_issued_at timestamp,
    client_id varchar(100) not null
);

create table redirects (
    id bigint,
    url varchar(500),
    client_id varchar(100)
);

create table scopes (
    id bigint,
    scope int,
    client bigint
);

create table grants (
    id bigint,
    grant_type varchar(50),
    client_id varchar(100)
);

create table auth_method (
    id bigint,
    auth_method varchar(50),
    client_id varchar(100)
);

create unique index ix_auth_username on authorities (id,authority);

create sequence users_SEQ start with 100 increment by 1;
create sequence clients_SEQ start with 100 increment by 50;
create sequence scopes_SEQ start with 100 increment by 50;
create sequence redirects_SEQ start with 100 increment by 50;
create sequence grants_SEQ start with 100 increment by 50;
create sequence auth_method_SEQ start with 100 increment by 50;