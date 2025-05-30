package com.fhxf.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum JobAcceptanceStatusEnum {
    PENDING(1, "待接受"),
    DOING(2,"进行中"),
    DONE(3, "已完成"),
    REJECTED(4, "已拒绝"),
    PAID(5, "已支付") ,
    CANCELLED(6, "已取消"),
    WAITCONFIRMEDONE(7, "待确认完成"),
    JOBOVER(8, "已结束"),
    ;
    @EnumValue // 标记数据库存的值是code
    private final int code;
    @JsonValue // 标记返回给前端的值是desc
    private final String desc;
    public static JobAcceptanceStatusEnum of(int code) {
        return Stream.of(values())
                .filter(bean -> bean.getCode() == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
    }



}
