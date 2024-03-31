package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.Track;
import com.cqcst.service.TrackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/track")
public class TrackController {

    @Resource
    TrackService trackService;

    @GetMapping("/{id}")
    public Result getTrackByOrderId(@PathVariable Integer id) {
      List<Track> list = trackService.getTrackByOrderId(id);
      return Result.success(list);
    }

}
