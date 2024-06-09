-- Tables - Authentication
drop table if exists verification;
drop table if exists permissions;
drop table if exists users;

create table users (
    id bigint,
    username varchar(50),
    password varchar(500),
    enabled boolean
);

create table permissions (
    id bigint,
    perm int,
    user_id bigint
);

create table verification (
    id bigint,
    code varchar(50),
    created timestamp,
    user_id int
);



-- Tables - Authorization
drop table if exists authorizations;
drop table if exists redirects;
drop table if exists scopes;
drop table if exists grants;
drop table if exists auths;
drop table if exists clients;

create table clients (
    id bigint,
    client_id varchar(100),
    name varchar(50),
    secret varchar(500),
    client_settings bytea,
    token_settings bytea
);

create table redirects (
    id bigint,
    url varchar(500),
    client bigint
);

create table scopes (
    id bigint,
    scope int,
    client bigint
);

create table grants (
    id bigint,
    grant_type int,
    client bigint
);

create table auths (
    id bigint,
    auth_type int,
    client bigint
);

-- create table authorizations (
--     id bigint,
--     principal_name varchar(200),
--     access_token_type varchar(100),
--     access_token_value bytea,
--     access_token_issued_at timestamp,
--     access_token_expires_at timestamp,
--     access_token_scopes varchar(1000),
--     refresh_token_value bytea,
--     refresh_token_issued_at timestamp,
--     client_id varchar(100) not null
-- );



-- indexes
drop index if exists ix_auth_username;

create unique index ix_auth_username on permissions (id, perm);



-- sequences
drop sequence if exists users_SEQ;
drop sequence if exists permissions_SEQ;
drop sequence if exists verification_SEQ;
drop sequence if exists clients_SEQ;
drop sequence if exists scopes_SEQ;
drop sequence if exists redirects_SEQ;
drop sequence if exists grants_SEQ;
drop sequence if exists auths_SEQ;

create sequence users_SEQ start with 100 increment by 50;
create sequence permissions_SEQ start with 100 increment by 50;
create sequence verification_SEQ start with 100 increment by 50;
create sequence clients_SEQ start with 100 increment by 50;
create sequence scopes_SEQ start with 100 increment by 50;
create sequence redirects_SEQ start with 100 increment by 50;
create sequence grants_SEQ start with 100 increment by 50;
create sequence auths_SEQ start with 100 increment by 50;



-- Functions
drop function if exists delete_old_verifications();

create or replace function delete_old_verifications() returns trigger
as $$
begin
    delete from verification
    where created < CURRENT_TIMESTAMP - interval '24 hours\';
    return null;
end;
$$ language plpgsql;




-- Triggers
drop trigger if exists verif_cleanup on verification;

create or replace trigger verif_cleanup
    after delete on verification
    for each row
execute procedure delete_old_verifications();
