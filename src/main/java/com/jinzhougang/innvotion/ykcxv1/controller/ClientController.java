package com.jinzhougang.innvotion.ykcxv1.controller;

import com.jinzhougang.innvotion.ykcxv1.entity.LoginRequest;
import com.jinzhougang.innvotion.ykcxv1.entity.LoginResponse;
import com.jinzhougang.innvotion.ykcxv1.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Zyang
 */
@Controller
@RequestMapping("/client")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            System.out.println(request.getUsername()+","+request.getPassword());
            LoginResponse response = userService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse(false, "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
