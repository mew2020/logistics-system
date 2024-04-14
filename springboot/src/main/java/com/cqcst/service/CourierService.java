package com.cqcst.service;

import com.cqcst.common.Result;
import com.cqcst.entity.Courier;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【courier】的数据库操作Service
* @createDate 2024-04-02 22:04:32
*/
public interface CourierService extends IService<Courier> {

    boolean changeDisabledByAdmin(Courier courier);

    boolean registerByAdmin(Courier courier);

    Result listBySiteId(Integer siteId);
}
