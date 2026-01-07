package com.jinzhougang.innvotion.ykcxv1.service;

import com.jinzhougang.innvotion.ykcxv1.entity.ExamResult;

public interface ExamResultService {
    ExamResult saveResult(String examNumber, Integer score);
}
