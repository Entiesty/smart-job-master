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
public enum EnterpriseCompanyTypeEnum {
    LIMITED_LIABILITY_COMPANY(1, "有限责任公司"),
    INDIVIDUAL_BUSINESS(1, "个体工商户"),
    PARTNERSHIP(1, "合伙企业"),
    SOLE_PROPRIETORSHIP(1, "个人独资企业"),
    SHAREHOLDERS_COMPANY(2, "股份有限公司");
    @EnumValue // 标记数据库存的值是code
    private final int code;
    @JsonValue // 标记返回给前端的值是desc
    private final String desc;




}