package com.egon.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.egon.analysis.convert.MonitorInfoConvert;
import com.egon.analysis.entity.po.CzRoad;
import com.egon.analysis.entity.po.MonitorInfo;
import com.egon.analysis.entity.vo.MonitorInfoVO;
import com.egon.analysis.mappers.MonitorInfoMapper;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.CzRoadQuery;
import com.egon.analysis.query.MonitorInfoQuery;
import com.egon.analysis.service.MonitorInfoService;
import com.egon.analysis.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author MrHao
* @description 针对表【t_monitor_info】的数据库操作Service实现
* @createDate 2024-05-10 21:59:14
*/
@Service
public class MonitorInfoServiceImpl extends BaseServiceImpl<MonitorInfoMapper, MonitorInfo>
    implements MonitorInfoService{

    @Override
    public PageResult<MonitorInfoVO> page(MonitorInfoQuery query) {
        IPage<MonitorInfo> page = baseMapper.selectPage(getPage(query), getWrapper(query));

        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(),page.getPages(), MonitorInfoConvert.INSTANCE.convertList(page.getRecords()));
    }

    private QueryWrapper<MonitorInfo> getWrapper(MonitorInfoQuery query) {
        QueryWrapper<MonitorInfo> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(query.getAreaId()), "area_id", query.getAreaId());
        wrapper.like(StringUtils.isNotEmpty(query.getName()), "name", query.getName());
        wrapper.isNotNull("name");
        return wrapper;
    }
}




