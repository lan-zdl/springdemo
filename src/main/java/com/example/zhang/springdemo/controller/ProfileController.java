package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.service.NotificationService;
import com.example.zhang.springdemo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public  String profile(HttpServletRequest request,
                           @PathVariable(name = "action") String action,
                           Model model,
                           @RequestParam(name ="page",defaultValue = "1") Integer page,
                           @RequestParam(name = "size",defaultValue = "5") Integer size){

        User user = (User)request.getSession().getAttribute("user");//得到页面上user的值
        if(user == null) {//没有user则返回根目录
            return "redirect:/";
        }

        if("questions".equals(action)) {//如果是问题展示问题列表
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getAccountId(), page,size);
            model.addAttribute("pagination",paginationDTO);

        }else if("replies".equals(action)) {//如果是回复展示回复通知的列表
            PaginationDTO paginationDTO = notificationService.list(user.getAccountId(),page,size);
            model.addAttribute("section","replies");
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("sectionName","最新回复");
        }

        return "profile";
    }
}
