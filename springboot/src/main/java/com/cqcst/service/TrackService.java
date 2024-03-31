package com.cqcst.service;

import com.cqcst.entity.Track;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【track】的数据库操作Service
* @createDate 2024-03-31 19:03:51
*/
public interface TrackService extends IService<Track> {

    List<Track> getTrackByOrderId(Integer id);
}
