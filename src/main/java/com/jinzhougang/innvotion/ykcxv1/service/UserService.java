package com.jinzhougang.innvotion.ykcxv1.service;

import com.jinzhougang.innvotion.ykcxv1.entity.LoginResponse;

public interface UserService {
    LoginResponse login(String username, String password);

    String hashPassword(String password);

    String generateToken(String username);

}
