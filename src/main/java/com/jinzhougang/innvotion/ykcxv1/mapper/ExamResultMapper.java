package com.jinzhougang.innvotion.ykcxv1.mapper;


import com.jinzhougang.innvotion.ykcxv1.entity.ExamResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface ExamResultMapper {
    // 检查考号是否已存在（MySQL 严格区分大小写，必须小写）
    @Select("SELECT COUNT(*) FROM exam_result WHERE examNumber = #{examNumber}")
    int countByExamNumber(String examNumber);

    // 保存成绩（MySQL 自动处理主键）
    @Insert("INSERT INTO exam_result(examNumber, score) VALUES(#{examNumber}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertExamResult(ExamResult examResult);
}