package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;

public interface BaseStorage<T> {

    public List<T> getAll();

    public T update(T data);

    public T create(T data);

    public void validate(T data);

    public Map<Integer, T> getStorage();

    public int getGeneratedId();
}
