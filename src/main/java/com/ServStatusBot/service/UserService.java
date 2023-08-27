package com.ServStatusBot.service;

import com.ServStatusBot.model.User;

public interface UserService {
    User saveUser(User user);

    User findByChatId(Long chatId);
}
