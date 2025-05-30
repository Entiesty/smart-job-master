package com.fhxf.domain.vo;

import com.fhxf.domain.po.ChatMessage;
import com.fhxf.domain.po.ChatSession;
import lombok.Data;

import java.util.List;

@Data
public class ChatVo  extends ChatSession{
    private List<ChatMessage> chatMessages;
}
