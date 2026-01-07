package com.jinzhougang.innvotion.ykcxv1.service;


import com.jinzhougang.innvotion.ykcxv1.datamodel.Comment;
import com.jinzhougang.innvotion.ykcxv1.datamodel.Moment;
import com.jinzhougang.innvotion.ykcxv1.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MomentService {
    private static int nextId = 1;
    private List<Moment> moments = new ArrayList<>();
    private Map<Integer, User> users = new HashMap<>();

    // 初始化用户
    public MomentService() {
        users.put(1, new User("zhangsan","11111",""));
        users.put(2, new User("admin","00000",""));
    }

    // 获取朋友圈列表（分页）
    public List<Moment> getMoments(int page, int size) {
        // 模拟数据
        if (moments.isEmpty()) {
            createTestMoments();
        }

        // 分页
        int start = page * size;
        int end = Math.min(start + size, moments.size());

        return moments.subList(start, end);
    }

    // 创建测试数据
    private void createTestMoments() {
        for (int i = 0; i < 20; i++) {
            Moment moment = new Moment();
            moment.setId(nextId++);
            moment.setUserId(i % 2 == 0 ? 1 : 2);
            moment.setContent("这是第" + i + "条朋友圈内容");
            moment.setImageUrls(i % 3 == 0 ? "https://placehold.co/600x400?text=Image1" : "");
            moment.setCreatedAt(LocalDateTime.now().minusDays(i));
            moment.setLikeCount(i % 5 == 0 ? 10 : i % 5);
            moment.setLiked(false);
            moments.add(moment);
        }
    }

    // 发布朋友圈
    public Moment createMoment(int userId, String content, List<String> imageUrls) {
        Moment moment = new Moment();
        moment.setId(nextId++);
        moment.setUserId(userId);
        moment.setContent(content);
        moment.setImageUrls(String.join(",", imageUrls));
        moment.setCreatedAt(LocalDateTime.now());
        moment.setLikeCount(0);
        moment.setLiked(false);
        moments.add(0, moment); // 新内容放在最前面
        return moment;
    }



    // 获取评论
    public List<Comment> getComments(int momentId) {
        // 模拟评论
        return Arrays.asList(
                new Comment(1, momentId, 2, "好美！", LocalDateTime.now().minusHours(1), users.get(2)),
                new Comment(2, momentId, 1, "赞一个", LocalDateTime.now().minusHours(2), users.get(1))
        );
    }

    // 添加评论
    public Comment addComment(int userId, int momentId, String content) {
        Comment comment = new Comment();
        comment.setId(nextId++);
        comment.setMomentId(momentId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(users.get(userId));
        return comment;
    }
}