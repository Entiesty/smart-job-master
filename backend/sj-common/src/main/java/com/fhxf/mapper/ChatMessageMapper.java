package com.fhxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fhxf.domain.po.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天消息Mapper接口
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}