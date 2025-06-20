package com.fhxf.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.fhxf.domain.constans.Constants;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.po.User;
import com.fhxf.global.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;
    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }


    public String createToken(Long userId, UserRoleEnum role, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload("id", userId)
                .setPayload("role",role)
                .setExpiresAt(new Date(System.currentTimeMillis() + Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息
     */
    public User parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new UnauthorizedException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayloads();
        User user = BeanUtil.copyProperties(userPayload, User.class);
        if (userPayload == null) {
            // 数据为空

            throw new UnauthorizedException("无效的token");
        }

        // 5.数据解析
        try {
           return user;
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new UnauthorizedException("无效的token");
        }
    }
}