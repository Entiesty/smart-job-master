package com.fhxf.controller;


import com.fhxf.domain.constans.Constants;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.global.exception.BusinessException;

/**
 * @className: ABaseController
 * @author: 李昌泉
 * @date: 2024/11/22 下午5:18
 * @Version: 1.0
 * @description:
 */

public class ABaseController extends BaseController {
    public boolean removeUserByTokenFromCookie() throws BusinessException {
        return super.removeUserByTokenFromCookie(getResponse(), Constants.TOKEN_WEB, Constants.REDIS_KEY_TOKEN);
    }

    public void saveTokenToCookie() {
        super.saveTokenToCookie( Constants.TOKEN_WEB, Constants.REDIS_KEY_TOKEN);
    }

    public UserVo getUserByTokenFromCookie() throws BusinessException {
        return super.getUserByTokenFromCookie(UserVo.class, getRequest(), Constants.TOKEN_WEB, Constants.REDIS_KEY_TOKEN);
    }



}
