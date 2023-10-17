package ru.yandex.practicum.filmorate.controllers;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


class UserControllerTest {

    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void validatePositive() {
        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.now().minusMonths(2))
                .email("yandex@practicum.ru")
                .build();
        userController.validate(user);
    }


    @Test
    void validateNegative() {
        User user = User.builder()
                .name("")
                .login("login")
                .birthday(LocalDate.now().plusMonths(2))
                .email("yandexpracticum.ru")
                .build();
        Assertions.assertThrows(ValidationException.class, () -> userController.validate(user));
    }
}

