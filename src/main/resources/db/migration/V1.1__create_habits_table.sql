create table habits (
                         id serial primary key,
                         name varchar(100) unique,
                         is_good boolean not null
);