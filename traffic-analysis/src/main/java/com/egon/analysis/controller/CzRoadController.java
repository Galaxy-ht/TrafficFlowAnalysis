package com.egon.analysis.controller;

import com.egon.analysis.entity.vo.CzRoadVO;
import com.egon.analysis.entity.vo.KVVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.CzRoadQuery;
import com.egon.analysis.service.CzRoadService;
import com.egon.analysis.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("czRoad")
public class CzRoadController extends BaseController{

    @Resource
    private CzRoadService czRoadService;

    @RequestMapping("page")
    public Result<PageResult<CzRoadVO>> page(@Valid CzRoadQuery query){
        PageResult<CzRoadVO> page = czRoadService.page(query);

        return Result.ok(page);
    }

    @RequestMapping("searchRoad")
    public Result<List<KVVO>> searchRoad(@Valid CzRoadQuery query){
        return Result.ok(czRoadService.searchRoad(query));
    }
}
