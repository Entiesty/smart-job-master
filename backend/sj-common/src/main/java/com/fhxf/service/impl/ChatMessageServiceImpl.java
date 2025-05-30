package com.fhxf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhxf.domain.po.ChatMessage;
import com.fhxf.mapper.ChatMessageMapper;
import com.fhxf.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author fhxf111
* @description 针对表【chat_message】的数据库操作Service实现
* @createDate 2025-04-29 23:49:03
*/
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




