package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.mapper.QuestionMapper;
import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.Question;
import com.example.zhang.springdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @PostMapping("/publish") //publish中的action以form表单提交后，这个方法接收到 title，description,tag的信息，并对他们进行判断是否为空
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        if(title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description == null || description == ""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if(tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        User user = null;
        Cookie[] cookies = request.getCookies();//使用cookie来判断此时是否进行登录

            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {//判断是否拿到token的cookie,通过token来获取user；
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);//在数据库中查找是否存在token这条记录
                    if (user != null) {//如果存在，将user放入session中；
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }

        if (user == null) {//如果user为NULL，说明未登录此时回到发布页面
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Question question = new Question();//构建question对象
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);
        return "redirect:/"; //发布成功，回到首页
    }
}
