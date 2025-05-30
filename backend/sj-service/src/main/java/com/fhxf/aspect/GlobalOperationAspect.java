package com.fhxf.aspect;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fhxf.annotation.Role;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.global.component.RedisComponent;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.utils.JwtTool;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class GlobalOperationAspect {
    @Resource
    private RedisComponent redisComponent;
    @Autowired
    private JwtTool jwtTool;

    public GlobalOperationAspect(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    @Before("@annotation(com.fhxf.annotation.Role)")
    public void interceptoDo(JoinPoint point) throws BusinessException {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Role interceptor = method.getAnnotation(Role.class);
        if (interceptor == null) {
            return;
        }
        check(interceptor.value());
    }
    private void check(UserRoleEnum value) throws BusinessException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException("请登录!");
        }
        UserRoleEnum role = jwtTool.parseToken(token).getRole();
        if (role != value){
            throw new BusinessException(role.getDesc()+"无法访问"+value.getDesc());
        }
    }
}