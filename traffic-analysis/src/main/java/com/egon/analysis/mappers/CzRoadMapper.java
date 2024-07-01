package com.egon.analysis.mappers;

import com.egon.analysis.entity.po.CzRoad;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author MrHao
* @description 针对表【cz_road】的数据库操作Mapper
* @createDate 2024-05-02 20:36:33
* @Entity com.egon.analysis.entity.po.CzRoad
*/
public interface CzRoadMapper extends BaseMapper<CzRoad> {

    void insertMonitor(Integer id, Integer maxspeed, String areaId, String point);
}




