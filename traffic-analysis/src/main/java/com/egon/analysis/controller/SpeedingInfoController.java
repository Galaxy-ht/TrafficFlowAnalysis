package com.egon.analysis.controller;

import com.egon.analysis.entity.vo.OverSpeedVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.SpeedingInfoQuery;
import com.egon.analysis.service.SpeedingInfoService;
import com.egon.analysis.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("speeding")
public class SpeedingInfoController extends BaseController{

    @Resource
    private SpeedingInfoService czRoadService;

    @RequestMapping("page")
    public Result<PageResult<OverSpeedVO>> page(@Valid SpeedingInfoQuery query){
        PageResult<OverSpeedVO> page = czRoadService.page(query);

        return Result.ok(page);
    }

//    @RequestMapping("page")
//    public Result<PageResult<OverSpeedVO>> getSpeedingCar(@Valid SpeedingInfoQuery query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//
//    @RequestMapping("page")
//    public Result<PageResult<OverSpeedVO>> getTaoPaiCar(@Valid SpeedingInfoQuery query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//
//    @RequestMapping("page")
//    public Result<PageResult<OverSpeedVO>> getDangerousCar(@Valid SpeedingInfoQuery query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
}
