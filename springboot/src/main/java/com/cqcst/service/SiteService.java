package com.cqcst.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqcst.entity.Site;

import java.util.List;

/**
* @author Administrator
* @description 针对表【site】的数据库操作Service
* @createDate 2024-03-25 16:33:55
*/
public interface SiteService extends IService<Site> {
    public Page<Site> selectPage(String name, Integer level);

    List<Site> selectByNameAndLevel(String name, Integer level);
}
