package com.jinzhougang.innvotion.ykcxv1.entity;

/**
 * @author Zyang
 */
// UserInfo.java - 用户信息数据传输对象
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String createTime;
    private String lastLoginTime;

    public UserInfo() {}

    public UserInfo(Long id, String username, String email, String createTime, String lastLoginTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
