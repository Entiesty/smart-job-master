package com.fhxf.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public enum UserRoleEnum {
    EMPLOYER(1, "worker"),
    ENTERPRISE(2, "enterprise");
    @EnumValue // 标记数据库存的值是code
    private final int code;
    @JsonValue // 标记返回给前端的值是desc
    private final String desc;

    public static UserRoleEnum getByCode(int code) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}