DROP SCHEMA notebridge1 cascade;

CREATE SCHEMA notebridge1;

create table users
(
    id            serial primary key,
    email         varchar(255)                                      not null unique,
    password      bytea                                             not null,
    full_name     varchar(30)                                       not null,
    country       varchar(50)  default 'UNKNOWN'::character varying not null,
    city          varchar(64)  default 'UNKNOWN'::character varying not null,
    password_salt bytea                                             not null,
    pfp_path      varchar(255) default 'UNKNOWN'::character varying not null,
    description   varchar(200) default 'UNKNOWN'::character varying not null,
    online        boolean      default false
);

create table admin
(
    id integer not null
        primary key
        references users
            on delete cascade
);

create table instrument
(
    id   serial
        primary key,
    name varchar(128) not null
        unique
);

create table skill
(
    id   serial
        primary key,
    name varchar(32) not null
        unique
);

create table message
(
    id           serial primary key,
    sender_id    integer      references users on delete set null,
    receiver_id  integer      references users on delete set null,
    message_text varchar(255) not null,
    timestamp    timestamp    not null,
    constraint sender_is_not_receiver
        check (sender_id <> receiver_id)
);

create table zipcode_coordinates
(
    zipcode   varchar(20)     not null primary key,
    latitude  numeric(19, 16) not null,
    longitude numeric(19, 16) not null
);

create table teacher
(
    id         integer                 not null primary key references users on delete cascade,
    experience varchar(25),
    avg_rating numeric(3, 1) default 0 not null
        constraint teacher_avg_rating_check
            check ((avg_rating >= (0)::numeric) AND (avg_rating <= (10)::numeric)),
    zipcode    varchar(64)             references zipcode_coordinates on delete set null,
    video_path varchar(255)
);

create table lesson
(
    id            serial primary key,
    teacher_id    integer        not null references teacher on delete cascade,
    price         numeric(10, 2) not null
        constraint lesson_price_check
            check (price >= (0)::numeric),
    instrument_id integer        not null references instrument on delete cascade,
    skill_id      integer        not null references skill,
    description   varchar(120)   not null,
    title         varchar(60)    not null
);

create table review
(
    id         serial primary key,
    teacher_id integer references teacher on delete cascade,
    student_id integer       references users on delete set null,
    rating     numeric(3, 1) not null
        constraint review_rating_check
            check ((rating >= (0)::numeric) AND (rating <= (10)::numeric)),
    comment    varchar(250)  not null,
    lesson_id  integer       not null references lesson on delete cascade,
    unique (lesson_id, student_id)
);

create table teacher_instruments
(
    teacher_id    integer not null references teacher on delete cascade,
    instrument_id integer not null references instrument on delete cascade,
    primary key (teacher_id, instrument_id)
);

create table teacher_schedule
(
    id         serial primary key,
    teacher_id integer references teacher on delete cascade,
    day        date,
    start_time time,
    end_time   time
);


create table booking
(
    id          serial primary key,
    student_id  integer not null references users on delete cascade,
    lesson_id   integer not null references lesson on delete cascade,
    schedule_id integer not null references teacher_schedule on delete cascade,
    is_canceled boolean not null,
    is_finished boolean not null,
    constraint booking_check
        check (NOT ((is_canceled IS TRUE) AND (is_finished IS TRUE)))
);

create table notification
(
    id           serial primary key,
    user_id      integer                       not null references users on delete cascade,
    text         varchar(255)                  not null,
    date         date,
    is_confirmed boolean,
    sender_id    integer default '-1'::integer not null references users,
    booking_id   integer                       not null references booking on delete cascade
);

create table payment
(
    id                serial primary key,
    booking_id        integer        not null unique references booking on delete cascade,
    amount            numeric(10, 2) not null,
    payment_timestamp timestamp,
    status            boolean        not null
);

create function is_payment_cost_equal_lesson_cost(_cost numeric, _id integer) returns boolean
    language plpgsql
as
$$
BEGIN
    RETURN
        EXISTS(SELECT lesson.price
               FROM lesson,
                    booking
               WHERE booking.id = _id
                 AND lesson.id = booking.lesson_id
                 AND lesson.price = _cost);
END
$$;

create function is_student_of_teacher(_student integer, _teacher integer) returns boolean
    language plpgsql
as
$$
BEGIN
    IF _student IS NULL then
        RETURN TRUE;
    ELSE
        RETURN
            EXISTS(SELECT b.id
                   FROM booking b
                            JOIN lesson l ON b.lesson_id = l.id
                   WHERE b.student_id = _student
                     AND l.teacher_id = _teacher
                     AND b.is_finished = true);
    END if;
END
$$;

create function is_instrument_taught_by_teacher(_instrument integer, _id integer) returns boolean
    language plpgsql
as
$$
BEGIN
    RETURN
        EXISTS(SELECT *
               FROM teacher_instruments
               WHERE instrument_id = _instrument
                 AND teacher_id = _id);
END
$$;

create function update_teacher_avg_rating() returns trigger
    language plpgsql
as
$$
BEGIN
    --     checking if trigger is on delete or insert/update, for delete we have to use the old value instead of the new value
    IF tg_op = 'DELETE' THEN
--         checking if the teacher has no more reviews left after deletion, then we should set the rating to 0 instead of null
        IF (NOT EXISTS (SELECT teacher.id, count(review.id)
                        FROM teacher
                                 join review on teacher.id = review.teacher_id
                        WHERE teacher.id = OLD.teacher_id
                        GROUP BY teacher.id)) THEN
            UPDATE teacher
            SET avg_rating = 0
            WHERE id = OLD.teacher_id;
            RETURN OLD;
--             teacher has one or more reviews left
        ELSE
            UPDATE teacher
            SET avg_rating = (SELECT AVG(r.rating)
                              FROM review r
                              WHERE r.teacher_id = OLD.teacher_id)
            WHERE id = OLD.teacher_id;
            RETURN OLD;
        end if;
--         we're dealing with an update or insert statement
    ELSE
        UPDATE teacher
        SET avg_rating = (SELECT AVG(r.rating)
                          FROM review r
                          WHERE r.teacher_id = NEW.teacher_id)
        WHERE id = NEW.teacher_id;

        RETURN NEW;
    end if;
END;
$$;

create trigger update_teacher_avg_rating_trigger
    after insert or update or delete
    on review
    for each row
execute procedure update_teacher_avg_rating();

create function max_lesson_is_three(_teacher integer) returns boolean
    language plpgsql
as
$$
BEGIN
    IF NOT EXISTS(SELECT *
                  FROM teacher t2
                           JOIN lesson l2 on t2.id = l2.teacher_id
                  WHERE t2.id = _teacher)
    THEN
        RETURN True;
    ELSE
        RETURN
            EXISTS(SELECT t.id, count(l.id)
                   FROM teacher t
                            JOIN lesson l on t.id = l.teacher_id
                   WHERE t.id = _teacher
                   GROUP BY t.id
                   HAVING COUNT(l.id) < 3);
    END IF;
END
$$;

create function set_default_pfp_path() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.pfp_path := '/media/' || NEW.id || '.jpg';
    RETURN NEW;
END;
$$;

create trigger set_default_pfp_path_trigger
    before insert
    on users
    for each row
execute procedure set_default_pfp_path();

create function validate_lesson_teacher(lesson_id integer) returns boolean
    language plpgsql
as
$$
BEGIN
    RETURN EXISTS (SELECT 1
                   FROM lesson
                   WHERE id = lesson_id
                     AND teacher_id = (SELECT teacher_id FROM lesson WHERE id = lesson_id));
END;
$$;

create function set_video_path() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.video_path := '/media/' || NEW.id || '.mp4';
    RETURN NEW;
END;
$$;

create trigger insert_video_path_trigger
    before insert
    on teacher
    for each row
execute procedure set_video_path();

alter table lesson
    add constraint max_lesson_is_three check (max_lesson_is_three(teacher_id));

alter table lesson
    add constraint check_instrument_taught_by_teacher
        check (is_instrument_taught_by_teacher(instrument_id, teacher_id));

alter table review
    add constraint check_is_student_of_teacher
        check (is_student_of_teacher(student_id, teacher_id));

alter table review
    add constraint valid_lesson_teacher
        check (validate_lesson_teacher(id, lesson_id));

