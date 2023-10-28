package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class InMemoryFilmStorage extends InMemoryBase<Film> implements FilmStorage {

    private static final LocalDate START_TIME = LocalDate.of(1895,12,28);

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Film update(Film data) {
        validate(data);
        if (!storage.containsKey(data.getId())) {
            throw new DataNotFoundException(String.format("Data %s not found", data));
        }
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public Film create(Film data) {
        validate(data);
        if (storage.containsKey(data.getId()))
            throw new DataAlreadyExistException(String.format("Data %s is already exist", data));
        data.setId(generatedId++);
        storage.put(data.getId(), data);
        return data;
    }

    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_TIME)) {
            throw new ValidationException("Invalid ReleaseDate.");
        }
    }

    @Override
    public Map<Integer, Film> getStorage() {
        return super.getStorage();
    }

    @Override
    public int getGeneratedId() {
        return super.getGeneratedId();
    }

    @Override
    public Film getFilm(Integer filmId) {
        if (!storage.containsKey(filmId)) {
            throw new DataNotFoundException("Фильм с таким id не найден.");
        }
        return storage.get(filmId);
    }
}
