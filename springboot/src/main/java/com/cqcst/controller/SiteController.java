package com.cqcst.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqcst.common.Result;
import com.cqcst.entity.Site;
import com.cqcst.service.SiteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/site")
public class SiteController {
    @Resource
    SiteService siteService;

    @GetMapping("/page")
    public Page<Site> selectPage(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value="level", required = false) Integer level) {
        Page<Site> res = siteService.selectPage(name, level);
        return res;
    }

    @GetMapping
    public Result selectByNameAndLevel(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value="level", required = false) Integer level) {
        List<Site> list = siteService.selectByNameAndLevel(name, level);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result DeleteById(@PathVariable Integer id) {
        boolean flag = siteService.removeById(id);
        return flag == true ? Result.success("success") : Result.error("error");
    }

    @PostMapping
    public Result add(@RequestBody Site site) {
        site.setId(null);
        boolean flag = siteService.save(site);
        return flag == true ? Result.success("success") : Result.error("error");
    }

    @PutMapping
    public Result update(@RequestBody Site site) {
        boolean flag = siteService.updateById(site);
        return flag == true ? Result.success("success") : Result.error("error");
    }
}
