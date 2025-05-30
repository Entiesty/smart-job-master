package com.fhxf.global.component;

import com.fhxf.config.JwtProperties;
import com.fhxf.domain.constans.Constants;
import com.fhxf.domain.vo.UserVo;
import com.fhxf.global.exception.BusinessException;
import com.fhxf.global.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("redisComponent")
@RequiredArgsConstructor
public class RedisComponent {
    private final RedisUtils redisUtils;
    public String saveCheckCode(String code) {
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, Constants.REDIS_EXPIRED_ONE_MIN * 10);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void removeCheckCode(String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void   saveUserToken(UserVo dto, String path) {
        dto.setExpirationTime(Long.valueOf(Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7));
        redisUtils.setex(path +   dto.getToken(), dto, Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7);
    }

    public boolean removeUserToken(String token, String path) throws BusinessException {
        Object o = redisUtils.get(path + token);
        if (null == o) {
            return false;
        }
        redisUtils.delete(path + token);
        return true;
    }

    public <T> T getUserToken(Class<T> tClass, String token, String path) throws BusinessException {
        T dto = (T) (redisUtils.get(path + token));
        if (null == dto) {
            return null;
        }
        if (tClass.isAssignableFrom(UserVo.class)) {
            ((UserVo) dto).setExpirationTime(Long.valueOf(Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7));
        }
        redisUtils.setex(path + token, dto, Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7);
        return (T) dto;
    }

    public void saveUserId(Long userId,String token) {
        redisUtils.setex(Constants.REDIS_KEY_USER_ID + userId, token, Constants.REDIS_EXPIRED_ONE_MIN * 60 * 24 * 7);
    }
    public String getUserId(Long userId) {
       return (String) redisUtils.get(Constants.REDIS_KEY_USER_ID + userId);
    }
}
