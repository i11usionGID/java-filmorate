package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre);
    }

    @Override
    public Genre getById(int id) {
        String sqlQuery = "select * from genres where g_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreDbStorage::createGenre, id);
        if (genres.size() != 1) {
            throw new DataNotFoundException(String.format("genre with id = %s not single", id));
        }
        return genres.get(0);
    }

    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("g_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
