package com.cqcst.service;

import com.cqcst.entity.Student;
import com.cqcst.exception.CustomException;
import com.cqcst.mapper.StudentMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service

public class StudentService {
    @Resource
    private StudentMapper studentMapper;
    public Student denglu(Student student){
        Student dbstudent=studentMapper.selectByUsername(student.getUsername(),student.getPassword(),student.getXuehao());
        if(dbstudent==null){
            throw new CustomException("账号或密码错误");
        }
        if(!student.getPassword().equals(dbstudent.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        return dbstudent;
    }

    public PageInfo<Student> selectx(Integer xuahao){
        List<Student> studentList=studentMapper.selectAll(xuahao);
        if(studentList==null){
            throw new CustomException("学号不存在");
        }
        return PageInfo.of(studentList);
    }

    public void register(Student student) {
        studentMapper.insert(student);
    }
}
