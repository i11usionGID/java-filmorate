package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

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
    public List<User> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::createUser);
    }

    @Override
    public User getUser(Integer userId) {
        String sqlQuery = "select * from users where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::createUser, userId);
        if (users.size() != 1) {
            throw new DataNotFoundException(String.format("user with id = %s not single", users));
        }
        return users.get(0);
    }

    @Override
    public User create(User data) {
        validate(data);
        String sqlQuery = "insert into users (email, login, name, birthday) " + "values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, data.getEmail());
            stmt.setString(2, data.getLogin());
            stmt.setString(3, data.getName());
            stmt.setDate(4, Date.valueOf(data.getBirthday()));
            return stmt;
        }, keyHolder);
        data.setId(keyHolder.getKey().intValue());
        return data;
    }

    @Override
    public User update(User data) {
        validate(data);
        User user = getUser(data.getId());
        if (user.getEmail() == null) {
            throw new DataNotFoundException(String.format("пользователя с id - %s не существет.", data.getId()));
        } else {
            String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
            jdbcTemplate.update(sqlQuery, data.getEmail(), data.getLogin(), data.getName(),
                    Date.valueOf(data.getBirthday()), data.getId());
            return data;
        }
    }

    private static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(LocalDate.parse(rs.getString("birthday")))
                .build();
    }
}
