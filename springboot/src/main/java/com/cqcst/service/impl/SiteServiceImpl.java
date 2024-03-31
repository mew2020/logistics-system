package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.entity.Site;
import com.cqcst.mapper.SiteMapper;
import com.cqcst.service.SiteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【site】的数据库操作Service实现
* @createDate 2024-03-25 16:33:55
*/
@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site>
    implements SiteService{

    @Override
    public Page<Site> selectPage(String name, Integer level) {

        return null;
    }

    @Override
    public List<Site> selectByNameAndLevel(String name, Integer level) {
        LambdaQueryWrapper<Site> queryWrapper = new LambdaQueryWrapper<>();
        if(!name.equals("")) {
            queryWrapper.like(Site::getName, name);
        }
        if(level != null) {
            queryWrapper.eq(Site::getLevel, level);
        }
        List<Site> list = list(queryWrapper);
        return list;
    }
}




