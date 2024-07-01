package com.egon.analysis.service;

import com.egon.analysis.entity.po.CzRoad;
import com.baomidou.mybatisplus.extension.service.IService;
import com.egon.analysis.entity.vo.CzRoadVO;
import com.egon.analysis.entity.vo.KVVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.CzRoadQuery;

import java.util.List;

/**
* @author MrHao
* @description 针对表【cz_road】的数据库操作Service
* @createDate 2024-05-02 20:36:33
*/
public interface CzRoadService extends IService<CzRoad> {
    PageResult<CzRoadVO> page(CzRoadQuery query);

    List<KVVO> searchRoad(CzRoadQuery query);

    CzRoad getFieldsById(Integer roadId, String fields);

    List<CzRoadVO> getAll();

    void insertMonitor();
}
