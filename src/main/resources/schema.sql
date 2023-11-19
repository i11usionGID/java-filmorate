create table if not exists USERS
(
id int not null primary key auto_increment,
email varchar(255) not null,
name varchar(255) not null,
login varchar(255) not null,
birthday date,
UNIQUE(email),
UNIQUE(login)
);

create table if not exists FRIENDS
(
user_id int references USERS(id),
friend_id int references USERS(id),
PRIMARY KEY(user_id, friend_id)
);

create table if not exists GENRES
(
g_id int not null primary key auto_increment,
genre_name varchar(255)
);

create table if not exists MPA
(
mpa_id int not null primary key auto_increment,
mpa_name varchar(255)
);

create table if not exists FILMS
(
id int not null primary key auto_increment,
name varchar(255),
description varchar(255),
release_date date,
duration int,
mpa_id int references MPA(mpa_id)
);

create table if not exists FILM_GENRES
(
film_id int references FILMS(id),
genre_id int references GENRES(g_id),
PRIMARY KEY (film_id, genre_id)
);

create table if not exists LIKES
(
user_id int references USERS(id),
film_id int references FILMS(id)
);
