package com.cqcst.mapper;

import com.cqcst.entity.Sh;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface TimeMapper {
//    这个是初审时间,初审挂三天才会在复审中显示（可以再写复审时间那个)
    @Select("select * from shenhe where datediff(#{time},chutime)>3 and zhuangtai=#{zhuangtai} and fushen=#{fushen} ")
    List<Sh> select(Date time, String zhuangtai, String fushen);

    @Select("select * from shenhe where datediff(#{time},chutime)>3 and zhuangtai=#{zhuangtai} and fushen=#{fushen} ")
    List<Sh> cha(Date time, String zhuangtai, String fushen);
}
