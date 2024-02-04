alter table profiles
rename column name to chat_id;

alter table profiles
add column if not exists token varchar(300);