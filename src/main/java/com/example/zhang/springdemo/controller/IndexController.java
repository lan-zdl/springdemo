package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/index")
    public String index(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies)
            if(cookie.getName().equals("token")) {//判断是否拿到token的cookie,通过token来获取user；
                String token = cookie.getValue();
                User user = userMapper.findByToken(token);//在数据库中查找是否存在token这条记录
                if (user != null) {//如果存在将user放入session中；
                    request.getSession().setAttribute("user", user);
                }
                break;
            }
        return "index";
        }
}
