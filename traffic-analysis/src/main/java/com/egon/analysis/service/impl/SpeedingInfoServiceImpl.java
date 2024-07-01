package com.egon.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.egon.analysis.entity.enums.AreaEnum;
import com.egon.analysis.entity.po.CzRoad;
import com.egon.analysis.entity.po.SpeedingInfo;
import com.egon.analysis.entity.vo.OverSpeedVO;
import com.egon.analysis.mappers.SpeedingInfoMapper;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.SpeedingInfoQuery;
import com.egon.analysis.service.AreaInfoService;
import com.egon.analysis.service.CzRoadService;
import com.egon.analysis.service.SpeedingInfoService;
import com.egon.analysis.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author MrHao
* @description 针对表【t_speeding_info】的数据库操作Service实现
* @createDate 2024-05-02 20:38:09
*/
@Service
public class SpeedingInfoServiceImpl extends BaseServiceImpl<SpeedingInfoMapper, SpeedingInfo>
    implements SpeedingInfoService{
    @Resource
    private CzRoadService czRoadService;

    @Resource
    private AreaInfoService areaInfoService;

    @Override
    public PageResult<OverSpeedVO> page(SpeedingInfoQuery query) {
        query.setOrderBy("action_time desc");
        IPage<SpeedingInfo> page = baseMapper.selectPage(getPage(query), getWrapper(query));
        List<OverSpeedVO> list = new ArrayList<>();
        List<SpeedingInfo> records = page.getRecords();
        for (SpeedingInfo record : records) {
            CzRoad road = czRoadService.getFieldsById(record.getRoadId(), "name,area_id");
            OverSpeedVO overSpeedVO = new OverSpeedVO();
            overSpeedVO.setId(record.getId());
            overSpeedVO.setCar(record.getCar());
            overSpeedVO.setRoadId(record.getRoadId());
            overSpeedVO.setRoadName(StringUtils.isEmpty(road.getName()) ? "未命名道路" : road.getName());
            overSpeedVO.setRealSpeed(record.getRealSpeed());
            overSpeedVO.setLimitSpeed(record.getLimitSpeed());
            overSpeedVO.setActionTime(new Date(record.getActionTime()*1000));
            overSpeedVO.setAreaName(AreaEnum.getById(road.getAreaId()));
            list.add(overSpeedVO);
        }
        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(),page.getPages(), list);
    }

    private QueryWrapper<SpeedingInfo> getWrapper(SpeedingInfoQuery query) {
        QueryWrapper<SpeedingInfo> wrapper = new QueryWrapper<>();
        wrapper.last(StringUtils.isNotEmpty(query.getOrderBy()), "order by " + query.getOrderBy());
        return wrapper;
    }
}




