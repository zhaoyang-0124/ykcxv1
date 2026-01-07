package com.jinzhougang.innvotion.ykcxv1.entity;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private Integer id;
    private Integer type; // 1-单选 2-多选 3-判断
    private String content;
    private List<Option> options;
    private List<String> correctAnswers;
    private Integer score; // 分值
}

