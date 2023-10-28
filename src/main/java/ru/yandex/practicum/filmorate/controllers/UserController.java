package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Getting users {}", userStorage.getStorage().values());
        return userStorage.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Creating user {}", user);
        return userStorage.create(user);
    }


    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user {}", user);
        return userStorage.update(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userStorage.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Integer id) {
        return userService.getFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{anotherId}")
    public List<User> getMutualFriends(@PathVariable("id") Integer id, @PathVariable("anotherId") Integer anotherId) {
        return userService.getMutualFriends(id, anotherId);
    }
}
