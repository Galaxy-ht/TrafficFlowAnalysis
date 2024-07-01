package com.egon.analysis.convert;

import com.egon.analysis.entity.po.SpeedingInfo;
import com.egon.analysis.entity.vo.SpeedingInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpeedingInfoConvert {
    SpeedingInfoConvert INSTANCE = Mappers.getMapper(SpeedingInfoConvert.class);

    @Mapping(target = "actionTime", expression = "java(vo.getActionTime().getTime())")
    SpeedingInfo convert(SpeedingInfoVO vo);

    @Mapping(target = "actionTime", expression = "java(new java.util.Date(entity.getActionTime()*1000))")
    SpeedingInfoVO convert(SpeedingInfo entity);

    List<SpeedingInfoVO> convertList(List<SpeedingInfo> list);

}
