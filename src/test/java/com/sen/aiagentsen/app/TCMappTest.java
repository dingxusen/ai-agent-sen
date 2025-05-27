package com.sen.aiagentsen.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TCMappTest {
    @Resource
    TCMapp tcmapp;
    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "我最近总是感到疲倦，晚上睡不好觉，白天也没精神。";
        String answer = tcmapp.doChat(message,chatId);
        //第二轮
        message = "睡觉之前喝点牛奶可以吗";
        answer = tcmapp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我应该怎么调理呢？";
        answer = tcmapp.doChat(message,chatId);
        Assertions.assertNotNull(answer);

    }



    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "我最近总是感到疲倦，晚上睡不好觉，白天也没精神，我应该怎么办？";
        TCMapp.TCMReport tcmReport = tcmapp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(tcmReport);

    }
}