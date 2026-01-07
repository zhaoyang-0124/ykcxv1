package com.jinzhougang.innvotion.ykcxv1.datamodel;

import com.jinzhougang.innvotion.ykcxv1.entity.User;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int momentId;
    private int userId;
    private String content;
    private LocalDateTime createdAt;
    private User user;

    public Comment(){

    }
    public Comment(int id,int momentId,int userId,String content,LocalDateTime createdAt,User user){

        this.id = id;
        this.momentId = momentId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
    }
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMomentId() { return momentId; }
    public void setMomentId(int momentId) { this.momentId = momentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}