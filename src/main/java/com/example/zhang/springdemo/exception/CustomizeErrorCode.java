package com.example.zhang.springdemo.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUNT("你要找的问题不在了，要不换个试试？");
    private  String message;

    @Override
    public String getMessage() {
        return message;
    }//重写接口中的方法

    CustomizeErrorCode(String message) {
        this.message = message;
    }
}
