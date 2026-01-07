package com.jinzhougang.innvotion.ykcxv1.service;

import com.jinzhougang.innvotion.ykcxv1.mapper.UserMapper;
import com.jinzhougang.innvotion.ykcxv1.entity.LoginResponse;
import com.jinzhougang.innvotion.ykcxv1.entity.User;
import com.jinzhougang.innvotion.ykcxv1.entity.UserInfo;
import com.sun.jmx.snmp.Timestamp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// UserService.java - 用户服务层
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

    // 登录逻辑处理
    @Override
    public LoginResponse login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginResponse(false, "用户名不能为空");
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResponse(false, "密码不能为空");
        }

        // 查找用户
        User user = userMapper.findByUsername(username);

        if (user == null) {
            // 用户不存在，创建新用户
            User newUser = new User(username, hashPassword(password), null);
            newUser.setId(System.currentTimeMillis()/1000);
            newUser.setEmail(null);
            newUser.setLastLoginTime(null);
            int result = userMapper.insert(newUser);
            System.out.println("到这里了");
            if (result > 0) {
                // 创建用户成功
                UserInfo userInfo = new UserInfo(
                        newUser.getId(),
                        newUser.getUsername(),
                        newUser.getEmail(),
                        newUser.getCreateTime(),
                        newUser.getLastLoginTime()
                );

                // 更新最后登录时间
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                userMapper.updateLastLoginTime(username, currentTime);

                return new LoginResponse(true, "用户注册并登录成功", generateToken(username), userInfo);
            } else {
                return new LoginResponse(false, "用户创建失败");
            }
        } else {
            // 用户存在，验证密码
            String hashedPassword = hashPassword(password);
            if (hashedPassword.equals(user.getPassword())) {
                // 密码正确，更新最后登录时间
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                userMapper.updateLastLoginTime(username, currentTime);

                UserInfo userInfo = new UserInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreateTime(),
                        user.getLastLoginTime()
                );

                return new LoginResponse(true, "登录成功", generateToken(username), userInfo);
            } else {
                // 密码错误
                return new LoginResponse(false, "密码错误");
            }
        }
    }

    // 密码加密方法（这里使用简单的MD5，实际应用中建议使用更安全的算法如BCrypt）
    @Override
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    // 生成认证令牌（简单实现，实际应用中建议使用JWT）
    @Override
    public String generateToken(String username) {
        return UUID.randomUUID().toString() + "_" + username;
    }
}