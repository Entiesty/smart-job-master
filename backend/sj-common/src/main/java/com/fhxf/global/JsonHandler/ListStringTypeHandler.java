package com.fhxf.global.JsonHandler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Field;
import java.util.List;

public   class ListStringTypeHandler extends AbstractJsonTypeHandler<List<String>> {
    public ListStringTypeHandler(Class<?> type) {
        super(type);
    }
    public ListStringTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }
    @Override
    public List<String> parse(String json) {
        return JSONUtil.toList(json, String.class);
    }
    @Override
    public String toJson(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }
}