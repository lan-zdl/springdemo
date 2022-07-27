package com.example.zhang.springdemo.mapper;

import com.example.zhang.springdemo.dto.QuestionQueryDTO;
import com.example.zhang.springdemo.model.Question;

import java.util.List;


public interface QuestionExtMapper {
    int incView(Question record);
    int incComment(Question record);
    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}