package com.jinzhougang.innvotion.ykcxv1.entity;


import lombok.Data;

@Data
public class ExamResult {
    private Long id;
    private String examNumber; // 考号（唯一标识）
    private Integer score;     // 成绩（0-100分）

    // 无参构造（JPA必需）
    public ExamResult() {}

    // 有参构造
    public ExamResult(String examNumber, Integer score) {
        this.examNumber = examNumber;
        this.score = score;
    }

    // Getters & Setters（实际项目建议用Lombok或IDE生成）
    public Long getId() { return id; }
    public String getExamNumber() { return examNumber; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}