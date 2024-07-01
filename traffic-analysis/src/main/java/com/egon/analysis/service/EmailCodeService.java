package com.egon.analysis.service;

import com.egon.analysis.entity.po.EmailCode;
import com.egon.analysis.entity.vo.EmailCodeVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.EmailCodeQuery;

import java.util.List;

/**
 * 邮箱验证码
 *
 * @author Tao MrHaoTao@gmail.com
 * @since 1.0.0 2023-08-19
 */
public interface EmailCodeService extends BaseService<EmailCode> {

    PageResult<EmailCodeVO> page(EmailCodeQuery query);

    void save(EmailCodeVO vo);

    void update(EmailCodeVO vo);

    void delete(List<Long> idList);

    void sendEmailCode(String email, Integer type);

    void checkCode(String email, String emailCode);
}