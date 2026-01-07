package com.jinzhougang.innvotion.ykcxv1.service;


import com.jinzhougang.innvotion.ykcxv1.entity.ExamResult;
import com.jinzhougang.innvotion.ykcxv1.mapper.ExamResultMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ExamResultServiceImpl implements ExamResultService{
    @Resource
    private ExamResultMapper mapper;

    @Override
    public ExamResult saveResult(String examNumber, Integer score) {
        if (score == null || score < 0 || score > 100) {
            throw new IllegalArgumentException("成绩必须在0-100之间");
        }
        if (mapper.countByExamNumber(examNumber) > 0) {
            throw new IllegalArgumentException("考号已存在");
        }
        ExamResult result = new ExamResult(examNumber, score);
        mapper.insertExamResult(result);
        return result;
    }
}