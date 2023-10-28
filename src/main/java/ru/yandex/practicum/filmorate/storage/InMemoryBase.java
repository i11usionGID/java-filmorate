package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.HashMap;
import java.util.Map;

public abstract class InMemoryBase<T extends BaseUnit> {
    protected final Map<Integer, T> storage = new HashMap<>();
    protected int generatedId = 1;

    public Map<Integer, T> getStorage() {
        return storage;
    }

    public int getGeneratedId() {
        return generatedId;
    }


}
