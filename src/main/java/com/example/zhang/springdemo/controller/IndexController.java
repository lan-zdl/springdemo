package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")  //index和根目录
    public String index(Model model,
                        @RequestParam(name ="page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name = "search",required = false) String search){

        PaginationDTO pagination = questionService.list1(search,page,size);
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);

        return "index";
    }
}
