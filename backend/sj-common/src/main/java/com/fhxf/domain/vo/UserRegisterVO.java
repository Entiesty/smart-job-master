package com.fhxf.domain.vo;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserRegisterVO implements Serializable {
    private static final long serialVersionUID = Long.parseLong(RandomUtil.randomNumbers(10));
    public String isSuccess;
}