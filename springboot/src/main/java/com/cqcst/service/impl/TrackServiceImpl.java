package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.entity.Track;
import com.cqcst.service.TrackService;
import com.cqcst.mapper.TrackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【track】的数据库操作Service实现
* @createDate 2024-03-31 19:03:51
*/
@Service
public class TrackServiceImpl extends ServiceImpl<TrackMapper, Track>
    implements TrackService{
    @Override
    public List<Track> getTrackByOrderId(Integer id) {
        LambdaQueryWrapper<Track> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Track::getOrderId,id).orderByDesc(Track::getCreateTime);
        return list(queryWrapper);
    }
}




