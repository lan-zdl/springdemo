package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileController {

    @ResponseBody
    @RequestMapping("/file/upload") //在Markdown编辑中开启图片上传功能图片上传请求到Controller
    public FileDTO upload(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("");
        return new FileDTO();
    }
}
