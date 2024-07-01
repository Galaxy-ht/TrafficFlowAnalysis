package com.egon.analysis.service;

import com.egon.analysis.entity.po.MonitorInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.egon.analysis.entity.vo.MonitorInfoVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.MonitorInfoQuery;

/**
* @author MrHao
* @description 针对表【t_monitor_info】的数据库操作Service
* @createDate 2024-05-10 21:59:14
*/
public interface MonitorInfoService extends BaseService<MonitorInfo> {
    PageResult<MonitorInfoVO> page(MonitorInfoQuery query);
}
