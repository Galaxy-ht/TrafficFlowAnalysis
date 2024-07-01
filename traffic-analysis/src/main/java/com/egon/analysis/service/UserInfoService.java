package com.egon.analysis.service;

import com.egon.analysis.entity.dto.SessionWebUserDto;
import com.egon.analysis.entity.po.UserInfo;
import com.egon.analysis.entity.vo.UserInfoVO;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.UserInfoQuery;

import java.util.List;

/**
 * 用户CRUD
 *
 * @author Tao MrHaoTao@gmail.com
 * @since 1.0.0 2023-08-16
 */
public interface UserInfoService extends BaseService<UserInfo> {

    PageResult<UserInfoVO> page(UserInfoQuery query);

    void save(UserInfoVO vo);

    void update(UserInfoVO vo);

    void delete(List<Long> idList);

    void register(String email, String nickName, String password, String emailCode);

    SessionWebUserDto login(String email, String password);

    void resetPwd(String email, String password, String emailCode);
}