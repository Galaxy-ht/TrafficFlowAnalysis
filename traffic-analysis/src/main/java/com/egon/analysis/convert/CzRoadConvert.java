package com.egon.analysis.convert;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.support.geo.Geometry;
import com.egon.analysis.entity.po.CzRoad;
import com.egon.analysis.entity.vo.CzRoadVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CzRoadConvert {
    CzRoadConvert INSTANCE = Mappers.getMapper(CzRoadConvert.class);
    @Mapping(target = "theGeom", expression = "java(com.alibaba.fastjson2.JSON.toJSONString(vo.getTheGeom()))")
    CzRoad convert(CzRoadVO vo);

    @Mapping(target = "theGeom", expression = "java(java.util.Arrays.stream(entity.getTheGeom().substring(entity.getTheGeom().indexOf(\"(\") + 1, entity.getTheGeom().lastIndexOf(\")\")).split(\",\")).map(element -> java.util.Arrays.asList(element.split(\" \"))).collect(java.util.stream.Collectors.toList()))")
    CzRoadVO convert(CzRoad entity);

    List<CzRoadVO> convertList(List<CzRoad> list);

}
