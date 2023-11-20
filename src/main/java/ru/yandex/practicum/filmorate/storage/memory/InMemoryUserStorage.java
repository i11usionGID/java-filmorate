package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryBase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Component
public class InMemoryUserStorage extends InMemoryBase<User> implements UserStorage {

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User update(User data) {
        validate(data);
        if (!storage.containsKey(data.getId())) {
            throw new DataNotFoundException(String.format("Data %s not found", data));
        }
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public User create(User data) {
        validate(data);
        if (storage.containsKey(data.getId()))
            throw new DataAlreadyExistException(String.format("Data %s is already exist", data));
        data.setId(generatedId++);
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public void validate(User data) {
        if (data.getName() == null || data.getName().isBlank()) {
            data.setName(data.getLogin());
        }
        if (data.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Invalid user birthday.");
        }
    }

    @Override
    public User getUser(Integer userId) {
        if (!storage.containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с таким id не найден.");
        }
        return storage.get(userId);
    }
}
