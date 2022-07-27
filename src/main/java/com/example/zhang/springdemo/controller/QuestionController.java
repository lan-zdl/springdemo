package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.CommentDTO;
import com.example.zhang.springdemo.dto.QuestionDTO;
import com.example.zhang.springdemo.enums.CommentTypeEnum;
import com.example.zhang.springdemo.service.CommentService;
import com.example.zhang.springdemo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name="id") Long id,
                           Model model) {

        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        //增加阅读数
        questionService.incView(id);
        model.addAttribute("question",questionDTO);//具体问题
        model.addAttribute("comments",comments);//相关评论
        model.addAttribute("relatedQuestions", relatedQuestions);//相关问题
        return "question";
    }
}
