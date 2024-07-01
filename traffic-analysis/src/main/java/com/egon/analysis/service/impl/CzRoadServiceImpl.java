package com.egon.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.egon.analysis.convert.CzRoadConvert;
import com.egon.analysis.entity.po.CzRoad;
import com.egon.analysis.entity.vo.CzRoadVO;
import com.egon.analysis.entity.vo.KVVO;
import com.egon.analysis.mappers.CzRoadMapper;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.CzRoadQuery;
import com.egon.analysis.service.CzRoadService;
import com.egon.analysis.utils.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
* @author MrHao
* @description 针对表【cz_road】的数据库操作Service实现
* @createDate 2024-05-02 20:36:33
*/
@Service
@CacheConfig(cacheNames = "czRoad")
public class CzRoadServiceImpl extends BaseServiceImpl<CzRoadMapper, CzRoad>
    implements CzRoadService{

    @Override
    public PageResult<CzRoadVO> page(CzRoadQuery query) {
        IPage<CzRoad> page = baseMapper.selectPage(getPage(query), getWrapper(query));

        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(),page.getPages(), CzRoadConvert.INSTANCE.convertList(page.getRecords()));
    }

    @Override
    public List<KVVO> searchRoad(CzRoadQuery query) {
        List<KVVO> list = new ArrayList<>();
        this.list(getSearchWrapper(query)).forEach(kv -> {
            KVVO kvvo = new KVVO();
            kvvo.setKey(String.valueOf(kv.getCode()));
            kvvo.setValue(kv.getName());
            list.add(kvvo);
        });
        return list;
    }

    @Override
    public CzRoad getFieldsById(Integer roadId, String fields) {
        QueryWrapper<CzRoad> wrapper = new QueryWrapper<>();
        wrapper.select(fields);
        wrapper.eq("id", roadId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<CzRoadVO> getAll() {
        return CzRoadConvert.INSTANCE.convertList(baseMapper.selectList(null));
    }

    @Override
    public void insertMonitor() {
        List<CzRoadVO> all = getAll();
        Random random = new Random();
        List<List<String>> theGeom = null;
        for (CzRoadVO czRoadVO : all) {
            theGeom = czRoadVO.getTheGeom();
            List<String> strings = theGeom.get(random.nextInt(theGeom.size()));
            String s = "'POINT(" + strings.get(1) + " " + strings.get(0) + ")'";
            baseMapper.insertMonitor(czRoadVO.getId(), czRoadVO.getMaxspeed(), czRoadVO.getAreaId(), s);
        }
    }

    private QueryWrapper<CzRoad> getSearchWrapper(CzRoadQuery query) {
        QueryWrapper<CzRoad> wrapper = new QueryWrapper<>();

        wrapper.select("DISTINCT name", "code");
        wrapper.like(StringUtils.isNotEmpty(query.getName()), "name", query.getName());
        wrapper.eq(StringUtils.isNotEmpty(query.getAreaId()), "area_id", query.getAreaId());
        wrapper.isNotNull("name");
        return wrapper;
    }

    private QueryWrapper<CzRoad> getWrapper(CzRoadQuery query) {
        QueryWrapper<CzRoad> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(query.getAreaId()), "area_id", query.getAreaId());
        wrapper.like(StringUtils.isNotEmpty(query.getName()), "name", query.getName());
        wrapper.isNotNull("name");
        return wrapper;
    }
}


