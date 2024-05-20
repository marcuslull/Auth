drop sequence if exists users_SEQ;
drop sequence if exists clients_SEQ;
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
    id bigserial not null primary key,
    client_id varchar(100) not null unique,
    name varchar(50) not null,
    secret varchar(500) not null,
    client_settings varchar(2000) not null ,
    token_settings varchar(2000) not null,
    avail_scopes varchar(2000) not null,
    auth_methods varchar(2000) not null,
    grant_types varchar(2000) not null,
    red_uris varchar(2000) not null,
    post_log_red_uris varchar(2000) not null,
    authorizations varchar(2000) not null
);

create table authorizations (
    id bigserial not null primary key,
    principal_name varchar(200) not null,
    access_token_type varchar(100) not null,
    access_token_value bytea not null,
    access_token_issued_at timestamp not null,
    access_token_expires_at timestamp not null,
    access_token_scopes varchar(1000) not null,
    refresh_token_value bytea default null,
    refresh_token_issued_at timestamp default null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    client_id varchar(100) not null,
    constraint fk_auth_clients foreign key(client_id) references clients(client_id)
);

create table redirects (
    id bigserial not null primary key,
    url varchar(500) not null,
    client_id varchar(100),
    constraint fk_red_clients foreign key (client_id) references clients(client_id)
);

create table scopes (
    id bigserial not null primary key,
    scope varchar(50) not null,
    client_id varchar(100) not null,
    constraint fk_scope_clients foreign key(client_id) references clients(client_id)
);

create table grants (
    id bigserial not null primary key,
    grant_type varchar(50) not null,
    client_id varchar(100) not null,
    constraint fk_grants_clients foreign key(client_id) references clients(client_id)
);

create table auth_method (
    id bigserial not null primary key,
    auth_method varchar(50) not null,
    client_id varchar(100) not null,
    constraint fk_grants_clients foreign key(client_id) references clients(client_id)
);

create unique index ix_auth_username on authorities (id,authority);

create sequence users_SEQ start with 100 increment by 1;