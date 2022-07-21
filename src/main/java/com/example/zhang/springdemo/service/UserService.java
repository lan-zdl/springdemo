package com.example.zhang.springdemo.service;

import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createorupdate(User user) {

        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0) {
            userMapper.insert(user);//将用户信息存储到数据库中
        }else {
            user = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(user.getId());
            userMapper.updateByExampleSelective(updateUser,example);
        }
    }
}
