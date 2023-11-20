package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final JdbcTemplate jdbcTemplate;
    private  FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(Integer id, Integer userId) {
       String sqlQuery = "insert into likes (user_id, film_id) " +
               "values (?, ?)";
       jdbcTemplate.update(sqlQuery, userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        String sqlQuery = "select user_id = ? from likes";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (userRows.next()) {
            jdbcTemplate.update("delete from likes where film_id = ? and user_id = ?", id, userId);
        } else {
            throw new DataNotFoundException(String.format("Пользователя с id - %s нет в базе данных", userId));
        }
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> films = filmStorage.getAll();
        if (films.size() < count) {
            count = films.size();
        }
        return films.stream()
                .sorted((p0, p1) -> {
                    int comp = compare(p0.getRate(), p1.getRate());
                    return -1 * comp;
                }).limit(count)
                .collect(Collectors.toList());
    }
}
