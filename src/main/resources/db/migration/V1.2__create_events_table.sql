create table events
(
    id       serial primary key,
    date     date,
    habit_id int,
    user_id int,
    constraint habit_id
        foreign key (habit_id)
            references habits (id),
    constraint user_id
        foreign key (user_id)
            references profiles (id),
    unique (date, habit_id, user_id)
);