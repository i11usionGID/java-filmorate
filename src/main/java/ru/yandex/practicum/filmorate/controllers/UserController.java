package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends BaseController<User> {
    @GetMapping
    public List<User> getAll() {
        log.info("Getting users {}", storage.values());
        return super.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user {}", user);
        return super.create(user);
    }


    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user {}", user);
        return super.update(user);
    }

    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid user birthday.");
        }
    }
}
