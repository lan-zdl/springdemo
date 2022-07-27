package com.example.zhang.springdemo.service;

import com.example.zhang.springdemo.dto.CommentDTO;
import com.example.zhang.springdemo.enums.CommentTypeEnum;
import com.example.zhang.springdemo.enums.NotificationTypeEnum;
import com.example.zhang.springdemo.enums.NotificationStatusEnum;
import com.example.zhang.springdemo.exception.CustomizeErrorCode;
import com.example.zhang.springdemo.exception.CustomizeException;
import com.example.zhang.springdemo.mapper.*;
import com.example.zhang.springdemo.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional //将insert方法体加上一个事务，当insert插入成功但是question评论数量增加失败时，整个事务回滚
    public void insert(Comment comment,User commentator) {

        if(comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            comment.setCommentCount(0L);
            commentMapper.insert(comment);

            //增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1L);
            commentExtMapper.incCommentCount(parentComment);

            //创建通知
            createNotify(comment,dbComment.getCommantor(),commentator.getName(), NotificationTypeEnum.REPLY_COMMENT,question.getTitle(), question.getId());
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            comment.setCommentCount(0L);
            //可能会因为网络的抖动问题，导致insert插入成功，但是在问题中的评论数增加失败，加入Transactional
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incComment(question);

            //创建通知
            createNotify(comment,question.getCreator(), commentator.getName(), NotificationTypeEnum.REPLY_QUESTION,question.getTitle(),question.getId());

        }
    }

    private void createNotify(Comment comment, String receiver, String notifierName, NotificationTypeEnum notificationType, String outerTitle,Long outerId) {
        if(receiver == comment.getCommantor()){
            return ;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommantor());
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();

        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");//按时间顺序倒序排列
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size() == 0) {
            return new ArrayList<>();
        }

        //获取去重的评论人
        Set<String> commentators = comments.stream().map(comment -> comment.getCommantor()).collect(Collectors.toSet());
        List<String> userAccountIds = new ArrayList();
        userAccountIds.addAll(commentators);

        //获取评论人并转为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdIn(userAccountIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<String,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getAccountId(), user -> user));

        //转换comment 为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommantor()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
