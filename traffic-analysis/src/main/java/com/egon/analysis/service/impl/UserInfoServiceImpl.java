package com.egon.analysis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.egon.analysis.config.AppConfig;
import com.egon.analysis.convert.UserInfoConvert;
import com.egon.analysis.entity.constants.Constants;
import com.egon.analysis.entity.dto.SessionWebUserDto;
import com.egon.analysis.entity.enums.UserStatusEnum;
import com.egon.analysis.entity.po.UserInfo;
import com.egon.analysis.entity.vo.UserInfoVO;
import com.egon.analysis.exception.FastException;
import com.egon.analysis.mappers.UserInfoDao;
import com.egon.analysis.page.PageResult;
import com.egon.analysis.query.UserInfoQuery;
import com.egon.analysis.service.EmailCodeService;
import com.egon.analysis.service.UserInfoService;
import com.egon.analysis.utils.RedisComponent;
import com.egon.analysis.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户CRUD
 *
 * @author Tao MrHaoTao@gmail.com
 * @since 1.0.0 2023-08-16
 */
@Service
@AllArgsConstructor
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoDao, UserInfo> implements UserInfoService {

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @Override
    public PageResult<UserInfoVO> page(UserInfoQuery query) {
        IPage<UserInfo> page = baseMapper.selectPage(getPage(query), getWrapper(query));

        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(),page.getPages(), UserInfoConvert.INSTANCE.convertList(page.getRecords()));
    }

    private QueryWrapper<UserInfo> getWrapper(UserInfoQuery query) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();

        return wrapper;
    }

    @Override
    public void save(UserInfoVO vo) {
        UserInfo entity = UserInfoConvert.INSTANCE.convert(vo);

        baseMapper.insert(entity);
    }

    @Override
    public void update(UserInfoVO vo) {
        UserInfo entity = UserInfoConvert.INSTANCE.convert(vo);

        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> idList) {
        removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfoEmail = this.getOne(new QueryWrapper<UserInfo>().eq("email", email));
        if (null != userInfoEmail) {
            throw new FastException("用户邮箱已注册");
        }

        UserInfo userInfoNickName = this.getOne(new QueryWrapper<UserInfo>().eq("nick_name", nickName));
        if (null != userInfoNickName) {
            throw new FastException("用户名已存在");
        }

        //校验邮箱验证码
        emailCodeService.checkCode(email, emailCode);

        String userId = StringUtils.getRandomNumber(Constants.LENGTH_10);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setEmail(email);
        userInfo.setNickName(nickName);
        userInfo.setPassword(StringUtils.encodeByMd5(password));
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        this.save(userInfo);

    }

    @Override
    public SessionWebUserDto login(String email, String password) {

        UserInfo userInfo = this.getOne(new QueryWrapper<UserInfo>().eq("email", email));

        if (null == userInfo || !userInfo.getPassword().equals(password)) {
            throw new FastException("账号或密码错误");
        }

        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new FastException("账号已被禁用");
        }

        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        this.updateById(updateInfo);

        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());
        sessionWebUserDto.setIsAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), email));

        return sessionWebUserDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {

        UserInfo userInfo = this.getOne(new QueryWrapper<UserInfo>().eq("email", email));

        if (null == userInfo) {
            throw new FastException("该账号尚未注册");
        }

        emailCodeService.checkCode(email, emailCode);

        userInfo.setPassword(StringUtils.encodeByMd5(password));
        this.updateById(userInfo);
    }

}