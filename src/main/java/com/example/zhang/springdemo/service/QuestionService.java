package com.example.zhang.springdemo.service;

import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.dto.QuestionDTO;
import com.example.zhang.springdemo.exception.CustomizeErrorCode;
import com.example.zhang.springdemo.exception.CustomizeException;
import com.example.zhang.springdemo.mapper.QuestionExtMapper;
import com.example.zhang.springdemo.mapper.QuestionMapper;
import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.Question;
import com.example.zhang.springdemo.model.QuestionExample;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuestionService {//组装question和user时候 需要一个中间层service

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;

        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
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

        //在数据库中定义了一个text类型的字段：使用selectByExampleWithRowbounds方法查出来的数据为null，应该使用selectByExampleWithBLOBsWithRowbounds
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(new QuestionExample(),new RowBounds(offset,size));//使用Mybatis的插件完成分页
        System.out.println(questions.get(0).getDescription());
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for(Question question : questions) {
            System.out.println(question.getDescription());
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andAccountIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
            User user = users.get(0);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        System.out.println(paginationDTO.getTotalPage());
        return paginationDTO;
    }

    public PaginationDTO list(String userAccountId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userAccountId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);

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

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userAccountId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for(Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andAccountIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
            User user = users.get(0);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);
        System.out.println(paginationDTO.getTotalPage());
        return paginationDTO;

    }

    public QuestionDTO getById(Integer id) {

        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUNT);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(question.getCreator());
        List<User> users = userMapper.selectByExample(userExample);
        User user = users.get(0);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createorupdate(Question question) {//编辑
        if(question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria()
                    .andIdEqualTo(question.getId());
            int update = questionMapper.updateByExampleSelective(updateQuestion,questionExample);
            if (update != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUNT);
            }
        }
    }

    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
