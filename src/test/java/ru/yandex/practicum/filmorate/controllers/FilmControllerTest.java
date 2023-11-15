package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    FilmController filmController;
    FilmStorage filmStorage;
    FilmService filmService;
    UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController((InMemoryFilmStorage) filmStorage, filmService);
    }

    @Test
    void negativeName() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/film-name-empty.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void negativeDescription() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/film-description-more-than-200.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void negativeReleaseDateEmpty() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/film-releaseDate-empty.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void negativeDuration() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/film-duration-negative.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void validatePositive() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1900,1,1))
                .duration(10)
                .build();
        filmStorage.validate(film);
    }

    @Test
    void validateNegativeReleaseDate() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800,1,1))
                .duration(10)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmStorage.validate(film));
    }



    private String getContentFromFile(String fileName) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + fileName).toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }
}