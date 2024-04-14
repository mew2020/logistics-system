package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.common.Result;
import com.cqcst.entity.Courier;
import com.cqcst.entity.Site;
import com.cqcst.entity.User;
import com.cqcst.service.CourierService;
import com.cqcst.mapper.CourierMapper;
import com.cqcst.service.SiteService;
import com.cqcst.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Administrator
* @description 针对表【courier】的数据库操作Service实现
* @createDate 2024-04-02 22:04:32
*/
@Service
public class CourierServiceImpl extends ServiceImpl<CourierMapper, Courier>
    implements CourierService{

    @Resource
    SiteService siteService;

    @Resource
    UserService userService;

    @Override
    public boolean changeDisabledByAdmin(Courier c) {
        Courier courier = getById(c.getId());
        if(courier == null) throw new RuntimeException("Courier Info Not Found");

        if(courier.getDisabled() == false) {
            //需要将用户的status设置为普通用户：1
            courier.setDisabled(true);
            User user = new User();
            user.setId(courier.getId());
            user.setStatus(1);
            userService.updateById(user);
            return updateById(courier);
        } else {
            //重新启用，需要将站点信息更新，并将其设置为快递员
            Site site = siteService.getById(c.getSiteId());
            if(site == null) throw new RuntimeException("Site Info Not Found");

            User user = new User();
            user.setId(courier.getId());
            user.setStatus(2);
            userService.updateById(user);

            courier.setDisabled(false);
            courier.setSiteId(site.getId());
            courier.setSiteName(site.getName());
            return updateById(courier);
        }
    }

    @Override
    public boolean registerByAdmin(Courier courier) {
        Site site = siteService.getById(courier.getSiteId());
        if(site == null) throw new RuntimeException("Site Info Not Found");
        courier.setSiteName(site.getName());
        return save(courier);
    }

    @Override
    public Result listBySiteId(Integer siteId) {
        if(siteId == null) return Result.error("参数错误");
        LambdaQueryWrapper<Courier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Courier::getSiteId, siteId);
        wrapper.eq(Courier::getDisabled, false);
        return Result.success(list(wrapper));
    }
}




