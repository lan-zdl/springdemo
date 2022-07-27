package com.example.zhang.springdemo.dto;

import com.example.zhang.springdemo.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long gmtModified;
    private Long gmtCreate;
    private String commantor;
    private Long likeCount;
    private String content;
    private User user;
    private Long commentCount;
}
