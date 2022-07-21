package com.example.zhang.springdemo.advice;

import com.example.zhang.springdemo.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomizeExceptionHandler {

    //返回一个渲染过的页面
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model) {
        if(e instanceof CustomizeException) {
            model.addAttribute("message",e.getMessage());
        }else {
            model.addAttribute("message","服务冒烟了，要不然你稍后再试试！");
        }
        return new ModelAndView("error");
    }

}
