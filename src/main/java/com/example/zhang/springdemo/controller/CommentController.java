package com.example.zhang.springdemo.controller;

import com.example.zhang.springdemo.dto.CommentCreateDTO;
import com.example.zhang.springdemo.dto.CommentDTO;
import com.example.zhang.springdemo.dto.ResultDTO;
import com.example.zhang.springdemo.enums.CommentTypeEnum;
import com.example.zhang.springdemo.exception.CustomizeErrorCode;
import com.example.zhang.springdemo.model.Comment;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.service.CommentService;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {


    @Autowired
    private CommentService commentService;

    @ResponseBody //通过response自动将对象序列化
    @RequestMapping(value = "/comment",method = RequestMethod.POST)//使用JSON的方式进行实现,页面传输一个JSON
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {//进行评论，将评论加入到数据库
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentCreateDTO == null || StringUtils.isNullOrEmpty(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommantor(user.getAccountId());
        comment.setLikeCount(0L);
        commentService.insert(comment,user);
            return ResultDTO.okOF();
    }

    //序列化:将数据对象转化为IO流
    @ResponseBody //通过response自动将对象序列化
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)//使用JSON的方式进行实现,页面传输一个JSON
    public  ResultDTO<List> comments(@PathVariable(name="id") Long id) {//对评论进行回复
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id,CommentTypeEnum.COMMENT);
        return ResultDTO.okOF(commentDTOS);
    }

}
