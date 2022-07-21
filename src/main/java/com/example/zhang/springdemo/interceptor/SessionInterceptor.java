package com.example.zhang.springdemo.interceptor;

import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {//拦截器拦截

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //程序执行之前执行
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length == 0)
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {//判断是否拿到token的cookie,通过token来获取user；
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);//在数据库中查找是否存在token这条记录
                    if (users.size() != 0) {//如果存在将user放入session中；
                        request.getSession().setAttribute("user", users.get(0));
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
