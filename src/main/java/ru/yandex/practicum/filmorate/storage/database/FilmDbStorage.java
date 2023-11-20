package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate START_TIME = LocalDate.of(1895,12,28);


    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_TIME)) {
            throw new ValidationException("Invalid ReleaseDate.");
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from films f join mpa m on f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::createFilm);
    }

    @Override
    public Film getFilm(Integer filmId) {
        String sqlQuery = "select * from films f join mpa m on f.mpa_id = m.mpa_id where f.id = ?";
        List<Film> film = jdbcTemplate.query(sqlQuery, this::createFilm, filmId);
        if (film.size() != 1) {
            throw new DataNotFoundException(String.format("film with id %s not single", filmId));
        }
        return film.get(0);
    }

    @Override
    public Film create(Film data) {
        validate(data);
        String sqlQuery = "insert into films (name, description, release_date, " +
                "duration, mpa_id)" + "values  (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, data.getName());
            stmt.setString(2, data.getDescription());
            stmt.setDate(3, Date.valueOf(data.getReleaseDate()));
            stmt.setInt(4, data.getDuration());
            stmt.setInt(5, data.getMpa().getId());
            return stmt;
        }, keyHolder);
        data.setId(keyHolder.getKey().intValue());
        String sqlQuery1 = "insert into film_genres (film_id, genre_id) " +
                "values (?, ?)";
        List<Genre> genres = new ArrayList<>(data.getGenres());
        jdbcTemplate.batchUpdate(sqlQuery1, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, data.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        return data;
    }

    @Override
    public Film update(Film data) {
        validate(data);
        Film film = getFilm(data.getId());
        if (film == null) {
            throw new DataNotFoundException(String.format("фильма с id - %s не существет.", data.getId()));
        } else {
            String sqlQuery = "update films set name = ?, description = ?, release_date = ?," +
                    " duration = ?, mpa_id = ? where id = ?";
            jdbcTemplate.update(sqlQuery, data.getName(), data.getDescription(), data.getReleaseDate(),
                    data.getDuration(), data.getMpa().getId(), data.getId());
            String sqlQuery1 = "delete from film_genres where film_id = ?";
            jdbcTemplate.update(sqlQuery1, data.getId());
            String sqlQuery2 = "insert into film_genres (film_id, genre_id) " +
                    "values (?, ?)";
            List<Genre> genres = new ArrayList<>(data.getGenres());
            jdbcTemplate.batchUpdate(sqlQuery2, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, data.getId());
                    ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
            return getFilm(data.getId());
        }
    }

    private Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = createMpa(rs, rowNum);
        String sql = "select * from film_genres fg join genres g ON fg.genre_id = g.g_id where film_id = ? ";
        List<Genre> genreList = jdbcTemplate.query(sql, (result, rowNum1) ->
               createGenre(result, rowNum1), rs.getInt("id"));
        Set<Genre> genres = new HashSet<>(genreList);
        String sqlLikes = "select count(user_id) from likes where film_id = ?";
        List<Integer> likesList = jdbcTemplate.queryForList(sqlLikes, Integer.class, rs.getInt("id"));
        int likes = likesList.get(0);
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("release_date")))
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .rate(likes)
                .build();
    }

    private Mpa createMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("g_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
