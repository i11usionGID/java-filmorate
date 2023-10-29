package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Set<Integer> getLikesFrom(Integer id) {
        return filmStorage.getFilm(id).getLikesFrom();
    }

    public void addLike(Integer id, Integer userId) {
        if (!filmStorage.getStorage().containsKey(id)) {
            throw new DataNotFoundException("Фильм с таким id не найден.");
        }
        if (!userStorage.getStorage().containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с таким id не найден.");
        }
        if (filmStorage.getFilm(id).getLikesFrom().contains(userStorage.getUser(userId))) {
            throw new DataAlreadyExistException("Пользователь уже оценил этот фильм.");
        }
        filmStorage.getFilm(id).addLike(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (!filmStorage.getStorage().containsKey(id)) {
            throw new DataNotFoundException("Фильм с таким id не найден.");
        }
        if (!userStorage.getStorage().containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с таким id не найден.");
        }
        if (!filmStorage.getFilm(id).getLikesFrom().contains(userStorage.getUser(userId).getId())) {
            throw new DataNotFoundException("Пользователь не ставил лайк этому фильму.");
        }
        filmStorage.getFilm(id).removeLike(userId);
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = filmStorage.getAll();
        return allFilms.stream()
                .sorted((p0, p1) -> {
                    Integer first = p0.getLikesFrom().size();
                    Integer second = p1.getLikesFrom().size();
            int comp = second.compareTo(first);
            return comp;
        }).limit(count).collect(Collectors.toList());
    }
}
