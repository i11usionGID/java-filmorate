package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendId){
        if(!userStorage.getStorage().containsKey(id) || !userStorage.getStorage().containsKey(friendId)){
            throw new DataNotFoundException("Пользователь с таким id не найден.");
        }
        userStorage.getUser(id).addFriend(friendId);
        userStorage.getUser(friendId).addFriend(id);
    }

    public void deleteFriend(Integer id, Integer friendId){
        userStorage.getUser(id).removeFriend(friendId);
        userStorage.getUser(friendId).removeFriend(id);
    }

    public List<User> getFriends(int id) {
        User user = userStorage.getUser(id);
        Set<Integer> listFriendId = user.getFriends();
        List<User> getFriend = new ArrayList<>();
        for (Integer integer : listFriendId) {
            getFriend.add(userStorage.getUser(integer));
        }
        return getFriend;
    }

    public List<User> getMutualFriends(Integer id, Integer anotherId) {
        List<User> userList = getFriends(id).stream()
                .filter(getFriends(anotherId)::contains)
                .collect(Collectors.toList());
        return userList;
    }
}
