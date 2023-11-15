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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    UserController userController;
    InMemoryUserStorage userStorage;
    UserService userService;

    @BeforeEach
    void setUp() {
        userController = new UserController(userStorage, userService);
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void negativeLoginEmpty() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/user-login-empty.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void negativeEmailWrong() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/user-email-wrong.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void negativeEmailEmpty() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getContentFromFile("controller/request/user-email-empty.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

   @Test
    void validatePositive() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.now().minusMonths(2))
                .email("yandex@practicum.ru")
                .build();
        userStorage.validate(user);
    }


    @Test
    void validateNegativeBirthday() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.now().plusMonths(2))
                .email("yandex@practicum.ru")
                .build();
        Assertions.assertThrows(ValidationException.class, () -> userStorage.validate(user));
    }

    @Test
    void validatePositiveEmptyName() {
        User user = User.builder()
                .name("")
                .login("login")
                .birthday(LocalDate.now().minusMonths(2))
                .email("yandex@practicum.ru")
                .build();
        userStorage.validate(user);
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

