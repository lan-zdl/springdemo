package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.NotificationDTO;
import com.example.zhang.springdemo.enums.NotificationTypeEnum;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public  String profile(HttpServletRequest request,
                           @PathVariable(name = "id") Long id){

        User user = (User)request.getSession().getAttribute("user");//得到页面上user的值
        if(user == null) {//没有user则返回根目录
            return "redirect:/";
        }
        NotificationDTO notificationDTO =  notificationService.read(id,user);

        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterId();
        } else {
            return "redirect:/";
        }

    }
}
