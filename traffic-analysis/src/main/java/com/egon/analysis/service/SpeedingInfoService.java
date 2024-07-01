package com.egon.analysis.service;

import com.egon.analysis.entity.po.SpeedingInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.egon.analysis.entity.vo.OverSpeedVO;
import com.egon.analysis.entity.vo.SpeedingInfoVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.SpeedingInfoQuery;

/**
* @author MrHao
* @description 针对表【t_speeding_info】的数据库操作Service
* @createDate 2024-05-02 20:38:09
*/
public interface SpeedingInfoService extends BaseService<SpeedingInfo> {
    PageResult<OverSpeedVO> page(SpeedingInfoQuery query);
}
