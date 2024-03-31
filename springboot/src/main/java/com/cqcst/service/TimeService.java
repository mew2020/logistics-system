package com.cqcst.service;

import com.cqcst.entity.Sh;
import com.cqcst.mapper.TimeMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
@Service
public class TimeService {
    @Resource
    private TimeMapper timeMapper;
    public PageInfo<Sh> select(Date time, String zhuangtai, String fushen) {
        List<Sh> sh=timeMapper.select(time,zhuangtai,fushen);
        return PageInfo.of(sh);
    }

    public PageInfo<Sh> cha(Date time, String zhuangtai, String fushen) {
        List<Sh> sh=timeMapper.cha(time,zhuangtai,fushen);
        return PageInfo.of(sh);
    }
}
