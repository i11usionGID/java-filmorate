package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(Integer id, Integer friendId) {
        if (userStorage.getUser(id) != null && userStorage.getUser(friendId) != null) {
            String sqlQuery = "insert into friends (user_id, friend_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, id, friendId);
        } else {
            throw new DataNotFoundException("Одного из пользователей не существет, перепроверьте вставленные id");
        }
    }

    public void deleteFriend(Integer id, Integer friendId) {
        String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public List<User> getFriends(int id) {
        String sqlQuery = "select friend_id from friends where user_id = ?";
        List<Integer> listFriends = jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
        return listFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer id, Integer anotherId) {
        return getFriends(id).stream()
                .filter(getFriends(anotherId)::contains)
                .collect(Collectors.toList());
    }
}
