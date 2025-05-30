package com.fhxf.domain.vo;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fhxf.domain.enums.UserRoleEnum;
import com.fhxf.domain.po.Certificates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @className: TokenUserInfoDto
 * @author: 李昌泉
 * @date: 2024/11/14 下午11:08
 * @Version: 1.0
 * @description:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    private static final long serialVersionUID = Long.parseLong(RandomUtil.randomNumbers(10));
    private Long id;
    private String username;
    private String avatar;
    private String contactEmail;
    private String contactPhone;
    private String introduction;
    private Long expirationTime;
    private String token;
    private UserRoleEnum role;
    private WokerVo worker;
    private EnterpriseVo enterprise;
}
