package com.egon.analysis.convert;

import com.egon.analysis.entity.po.MonitorInfo;
import com.egon.analysis.entity.vo.MonitorInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonitorInfoConvert {
    MonitorInfoConvert INSTANCE = Mappers.getMapper(MonitorInfoConvert.class);
    @Mapping(target = "theGeom", expression = "java(com.alibaba.fastjson2.JSON.toJSONString(vo.getTheGeom()))")
    MonitorInfo convert(MonitorInfoVO vo);

    @Mapping(target = "theGeom", expression = "java(java.util.Arrays.stream(entity.getTheGeom().substring(entity.getTheGeom().indexOf(\"(\") + 1, entity.getTheGeom().lastIndexOf(\")\")).split(\",\")).map(element -> java.util.Arrays.asList(element.split(\" \"))).collect(java.util.stream.Collectors.toList()))")
    MonitorInfoVO convert(MonitorInfo entity);

    List<MonitorInfoVO> convertList(List<MonitorInfo> list);

}
