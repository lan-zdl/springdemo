package com.example.zhang.springdemo.service;

import com.example.zhang.springdemo.dto.PaginationDTO;
import com.example.zhang.springdemo.dto.QuestionDTO;
import com.example.zhang.springdemo.dto.QuestionQueryDTO;
import com.example.zhang.springdemo.exception.CustomizeErrorCode;
import com.example.zhang.springdemo.exception.CustomizeException;
import com.example.zhang.springdemo.mapper.QuestionExtMapper;
import com.example.zhang.springdemo.mapper.QuestionMapper;
import com.example.zhang.springdemo.mapper.UserMapper;
import com.example.zhang.springdemo.model.Question;
import com.example.zhang.springdemo.model.QuestionExample;
import com.example.zhang.springdemo.model.User;
import com.example.zhang.springdemo.model.UserExample;
import com.mysql.cj.util.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class QuestionService {//组装question和user时候 需要一个中间层service

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list1(String search,Integer page, Integer size) {

        if(!StringUtils.isNullOrEmpty(search)) {
            String[] tags =  search.split(" ");//以“,”分离标签
            search = Arrays.stream(tags).collect(Collectors.joining("|"));//将分离的标签使用|连接
        }

        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;

        QuestionQueryDTO questionQueryDTO =new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
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
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        //在数据库中定义了一个text类型的字段：使用selectByExampleWithRowbounds方法查出来的数据为null，应该使用selectByExampleWithBLOBsWithRowbounds
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);//使用Mybatis的插件完成分页
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
        paginationDTO.setData(questionDTOList);
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

        paginationDTO.setData(questionDTOList);
        System.out.println(paginationDTO.getTotalPage());
        return paginationDTO;

    }

    public QuestionDTO getById(Long id) {

        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
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
            question.setLikeCount(0);
            question.setViewCount(0);
            question.setCommentCount(0);
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
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

        public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isNullOrEmpty(queryDTO.getTag())){
            return new ArrayList<>();
        }

        String[] tags =  queryDTO.getTag().split(",");//以“,”分离标签
        String regexTag = Arrays.stream(tags).collect(Collectors.joining("|"));//将分离的标签使用|连接
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexTag); //将各个标签用|连接后放入question对象的tag中

        //在数据库中查询相关联问题
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q ->{ //将查询出的问题放入questionDTO中，并questionDTO加入列表中
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect((Collectors.toList()));
        return questionDTOS;

    }
}
