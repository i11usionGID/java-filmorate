package ru.yandex.practicum.filmorate.controllers;

import ru.yandex.practicum.filmorate.exceptions.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController<T extends BaseUnit> {
    public final Map<Integer, T> storage = new HashMap<>();
    private int generatedId = 1;

    public T create(T data) {
        validate(data);
        if (storage.containsKey(data.getId()))
            throw new DataAlreadyExistException(String.format("Data %s is already exist", data));
        data.setId(generatedId++);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(T data) {
        validate(data);
        if (!storage.containsKey(data.getId())) {
            throw new DataNotFoundException(String.format("Data %s not found", data));
        }
        storage.put(data.getId(), data);
        return data;
    }

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    public abstract void validate(T data);
}
