package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileComtroller {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/profile/{action}")
    public  String profile(HttpServletRequest request,
                           @PathVariable(name = "action") String action,
                           Model model,
                           @RequestParam(name ="page",defaultValue = "1") Integer page,
                           @RequestParam(name = "size",defaultValue = "5") Integer size){
        User user = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length == 0){
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {//判断是否拿到token的cookie,通过token来获取user；
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);//在数据库中查找是否存在token这条记录
                    if (user != null) {//如果存在将user放入session中；
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
        }
        if(user == null) {
            return "redirect:/";
        }

        if("question".equals(action)) {
            model.addAttribute("section","question");
            model.addAttribute("sectionName","我的提问");
        }else if("replies".equals(action)) {
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
        }

        PaginationDTO paginationDTO = questionService.list(user.getId(), page,size);
        model.addAttribute("pagination",paginationDTO);

        return "profile";
    }
}
