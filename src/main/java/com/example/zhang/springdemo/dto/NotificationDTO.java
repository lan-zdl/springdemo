package com.example.zhang.springdemo.dto;

import com.example.zhang.springdemo.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private String notifier;
    private String notifierName;
    private String outerTitle;
    private String typeName;
    private Long outerId;
    private Integer type;
}
