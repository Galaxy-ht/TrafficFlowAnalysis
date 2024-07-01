package com.egon.analysis.convert;

import com.egon.analysis.entity.po.EmailCode;
import com.egon.analysis.entity.vo.EmailCodeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 邮箱验证码
*
* @author Tao MrHaoTao@gmail.com
* @since 1.0.0 2023-08-19
*/
@Mapper(componentModel = "spring")
public interface EmailCodeConvert {
    EmailCodeConvert INSTANCE = Mappers.getMapper(EmailCodeConvert.class);

    EmailCode convert(EmailCodeVO vo);

    EmailCodeVO convert(EmailCode entity);

    List<EmailCodeVO> convertList(List<EmailCode> list);

}