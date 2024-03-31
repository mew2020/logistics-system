package com.cqcst.mapper;

import com.cqcst.entity.Admin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface AdminMapper {
    @Select("select * from admin where username=#{username}")
    Admin selectByUsername(String username);

    @Insert("insert into admin (username,password,role,xueyuan) values (#{username},#{password},#{role},#{xueyuan})")
    void insert(Admin admin);
}
