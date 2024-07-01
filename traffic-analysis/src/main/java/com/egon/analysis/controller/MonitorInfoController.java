package com.egon.analysis.controller;

import com.egon.analysis.entity.vo.MonitorInfoVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.MonitorInfoQuery;
import com.egon.analysis.service.MonitorInfoService;
import com.egon.analysis.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("monitor")
public class MonitorInfoController extends BaseController {
    @Resource
    private MonitorInfoService monitorInfoService;

    @RequestMapping("page")
    public Result<PageResult<MonitorInfoVO>> page(@Valid MonitorInfoQuery query){
        PageResult<MonitorInfoVO> page = monitorInfoService.page(query);

        return Result.ok(page);
    }
}
