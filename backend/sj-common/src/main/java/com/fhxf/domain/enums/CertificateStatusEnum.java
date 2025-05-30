package com.fhxf.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CertificateStatusEnum {
    PENDING(1, "待认证"),
    ACCEPTED(2, "已认证");
    @EnumValue // 标记数据库存的值是code

    private final int code;
    @JsonValue // 标记返回给前端的值是desc
    private final String desc;

}
