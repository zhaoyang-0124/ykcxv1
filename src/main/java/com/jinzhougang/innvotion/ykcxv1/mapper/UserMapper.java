package com.jinzhougang.innvotion.ykcxv1.mapper;

import com.jinzhougang.innvotion.ykcxv1.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// UserMapper.java - 用户数据访问接口
@Mapper
public interface UserMapper {
    // 根据用户名查询用户
    User findByUsername(@Param("username") String username);

    // 插入新用户
    int insert(User user);

    // 更新用户最后登录时间
    int updateLastLoginTime(@Param("username") String username, @Param("lastLoginTime") String lastLoginTime);
}
