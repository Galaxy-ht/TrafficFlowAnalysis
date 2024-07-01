package com.egon.analysis.mappers;

import com.egon.analysis.entity.po.EmailCode;
import org.apache.ibatis.annotations.Param;

/**
* 邮箱验证码
*
* @author Tao MrHaoTao@gmail.com
* @since 1.0.0 2023-08-19
*/

public interface EmailCodeDao extends BaseDao<EmailCode> {

    void disableEmailCode(@Param("email") String email);
}