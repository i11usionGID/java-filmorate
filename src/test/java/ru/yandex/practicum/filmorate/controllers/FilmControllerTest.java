package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void setUp(){
        filmController = new FilmController();
    }

    @Test
    void validatePositive() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1900,1,1))
                .duration(10)
                .build();
        filmController.validate(film);
    }

    @Test
    void validateNegative() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800,1,1))
                .duration(10)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmController.validate(film));
    }
}