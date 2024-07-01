//package com.egon.analysis.controller;
//
//import com.egon.analysis.entity.vo.OverSpeedVO;
//import com.egon.analysis.page.PageResult;
//import com.egon.analysis.query.Query;
//import com.egon.analysis.service.SpeedingInfoService;
//import com.egon.analysis.utils.Result;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("speeding")
//public class TrafficFlowController extends BaseController{
//
//    @Resource
//    private SpeedingInfoService czRoadService;
//
//
//    @RequestMapping("page")
//    public Result<PageResult<FlowVO>> getTrafficFlow(@Valid Query query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//
//    @RequestMapping("page")
//    public Result<PageResult<FlowVO>> getPreTrafficFlow(@Valid Query query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//
//    @RequestMapping("page")
//    public Result<PageResult<StarVO>> getPreTrafficStar(@Valid Query query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//
//    @RequestMapping("page")
//    public Result<PageResult<StarVO>> getTrafficStar(@Valid Query query){
//        PageResult<OverSpeedVO> page = czRoadService.page(query);
//
//        return Result.ok(page);
//    }
//}
