package com.egon.analysis.controller;

import com.egon.analysis.annotation.GlobalInterceptor;
import com.egon.analysis.annotation.VerifyParam;
import com.egon.analysis.entity.constants.Constants;
import com.egon.analysis.entity.dto.CreateImageCode;
import com.egon.analysis.entity.dto.SessionWebUserDto;
import com.egon.analysis.entity.enums.VerifyRegexEnum;
import com.egon.analysis.entity.po.UserInfo;
import com.egon.analysis.exception.FastException;
import com.egon.analysis.service.UserInfoService;
import com.egon.analysis.utils.Result;
import com.egon.analysis.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用户CRUD
 *
 * @author Tao MrHaoTao@gmail.com
 * @since 1.0.0 2023-08-16
 */
@RestController
@RequestMapping("userInfo")
//@Tag(name="用户CRUD")
@AllArgsConstructor
public class UserInfoController extends BaseController {

    private final UserInfoService userInfoService;

    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    /**
     * 根据请求类型返回验证码并存入session
     *
     * @param HttpServletResponse response
     * @param HttpSession         session
     * @param Integer             type 0:登录注册  1:邮箱验证码发送  默认0
     * @return void
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public Result<String> register(HttpSession session,
                                   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                   @VerifyParam(required = true) String nickName,
                                   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8) String password,
                                   @VerifyParam(required = true) String checkCode,
                                   @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new FastException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return Result.ok("邮件发送成功");
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public Result<SessionWebUserDto> login(HttpSession session,
                                           @VerifyParam(required = true) String email,
                                           @VerifyParam(required = true) String password,
                                           @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new FastException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return Result.ok(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }


    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public Result<String> resetPwd(HttpSession session,
                                   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 10) String password,
                                   @VerifyParam(required = true) String checkCode,
                                   @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new FastException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return Result.ok("密码修改成功");
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public Result<Object> updatePassword(HttpSession session,
                                         @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        UserInfo userInfo = userInfoService.getById(sessionWebUserDto.getUserId());
        userInfo.setPassword(StringUtils.encodeByMd5(password));
        userInfoService.updateById(userInfo);
        return Result.ok(null);
    }

    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public Result<SessionWebUserDto> getUserInfo(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);

        return Result.ok(sessionWebUserDto);
    }

    @RequestMapping("/logout")
    public Result<Object> logout(HttpSession session) {
        session.invalidate();
        return Result.ok(null);
    }


}