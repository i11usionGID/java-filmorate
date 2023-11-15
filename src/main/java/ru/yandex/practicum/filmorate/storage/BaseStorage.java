package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface BaseStorage<T> {

    List<T> getAll();

    T update(T data);

    T create(T data);

    void validate(T data);

    Map<Integer, T> getStorage();

    int getGeneratedId();
}
