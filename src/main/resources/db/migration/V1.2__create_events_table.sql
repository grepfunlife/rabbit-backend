create table events
(
    id       serial primary key,
    date     date,
    habit_id int,
    constraint habit_id
        foreign key (habit_id)
            references habits (id)
);