package com.cqcst.mapper;

import com.cqcst.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;
public interface StudentMapper {
    @Select("select * from student where username=#{username} and password=#{password} and xuehao=#{xuehao}")
    Student selectByUsername(String username,String password,Integer xuehao);

    @Insert("insert into student (username,password,nickname,xuehao,xname) values (#{username},#{password},#{nickname},#{xuehao},#{xname})")
    void insert(Student student);

    @Select("select * from chengji where xuehao=#{xuehao}")
    List<Student> selectAll(Integer xuahao);
}
