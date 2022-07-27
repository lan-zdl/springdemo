package com.example.zhang.springdemo.service;

import com.example.zhang.springdemo.dto.NotificationDTO;
import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.enums.NotificationStatusEnum;
import com.example.zhang.springdemo.enums.NotificationTypeEnum;
import com.example.zhang.springdemo.exception.CustomizeErrorCode;
import com.example.zhang.springdemo.exception.CustomizeException;
import com.example.zhang.springdemo.mapper.NotificationMapper;
import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.Notification;
import com.example.zhang.springdemo.model.NotificationExample;
import com.example.zhang.springdemo.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(String accountId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(accountId);
        Integer totalCount = (int)notificationMapper.countByExample(notificationExample);

        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage,page);

        //size*(page-1)
        Integer offset = Math.max(0,size * (page - 1));

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(accountId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));
        if(notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        for(Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOs.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOs);
        return paginationDTO;
    }

    public Long unreadCount(String userAccountId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userAccountId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {

        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }

        //equals 与 == 的区别
        if(!notification.getReceiver().equals(user.getAccountId())) {//判断通知是否是自己的
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
