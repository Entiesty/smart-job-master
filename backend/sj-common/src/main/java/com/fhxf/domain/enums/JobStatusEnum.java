package com.fhxf.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatusEnum {
    RECRUITING(1, "招募中"),
    ONGOING(2, "进行中"),
    ENDED(3, "已结束");
    @EnumValue // 标记数据库存的值是code
    private final int code;
    @JsonValue // 标记返回给前端的值是desc
    private final String desc;
}
