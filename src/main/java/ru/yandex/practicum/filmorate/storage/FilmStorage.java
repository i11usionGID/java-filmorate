package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    List<Film> getAll();

    Film update(Film data);

    Film create(Film data);

    void validate(Film data);

    Film getFilm(Integer filmId);
}
