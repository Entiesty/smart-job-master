package com.fhxf.interceptor;


import com.fhxf.domain.constans.Constants;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.global.component.RedisComponent;
import com.fhxf.global.exception.UnauthorizedException;
import com.fhxf.utils.JwtTool;
import com.fhxf.utils.UserContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;


@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final RedisComponent redisComponent;
    private final JwtTool jwtTool;
    private final  HttpServletResponse  response;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的 token
        String token = request.getHeader("authorization");
        if (token == null){
            // 从cookie中获取token
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (token == null){
            throw new UnauthorizedException("请登录!");
        }
        // 2.校验token
        UserVo userToken = redisComponent.getUserToken(UserVo.class, token, Constants.REDIS_KEY_TOKEN);
        if (userToken == null){
            throw new UnauthorizedException("登录过期,请重新登录!");
        }
        // 3.存入上下文
        UserContext.setUser(userToken.getId());
        // 4.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
