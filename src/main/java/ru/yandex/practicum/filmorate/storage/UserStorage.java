package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User update(User data);

    User create(User data);

    void validate(User data);

    User getUser(Integer userId);
}
