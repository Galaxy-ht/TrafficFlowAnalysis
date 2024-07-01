package com.egon.analysis.convert;

import com.egon.analysis.entity.po.UserInfo;
import com.egon.analysis.entity.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 用户CRUD
*
* @author Tao MrHaoTao@gmail.com
* @since 1.0.0 2023-08-16
*/
@Mapper(componentModel = "spring")
public interface UserInfoConvert {
    UserInfoConvert INSTANCE = Mappers.getMapper(UserInfoConvert.class);

    UserInfo convert(UserInfoVO vo);

    UserInfoVO convert(UserInfo entity);

    List<UserInfoVO> convertList(List<UserInfo> list);

}