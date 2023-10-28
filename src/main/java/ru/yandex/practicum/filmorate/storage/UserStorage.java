package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage extends BaseStorage<User> {
    public User getUser(Integer userId);
}
