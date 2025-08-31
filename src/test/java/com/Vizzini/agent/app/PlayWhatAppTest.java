package com.Vizzini.agent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@Slf4j
@SpringBootTest
class PlayWhatAppTest {

  @Resource
  private PlayWhatApp playWhatApp;

  @Test
    void testDoChat() {
    String chatId = UUID.randomUUID().toString();
    // 第一轮
    String message = "我是元宝";
    String answer = playWhatApp.doChat(message, chatId);
    Assertions.assertNotNull(answer);
    // 第二轮
    message = "我是豆包";
    answer = playWhatApp.doChat(message, chatId);
    Assertions.assertNotNull(answer);
    // 第三轮
    message = "那我到底是谁";
    answer = playWhatApp.doChat(message, chatId);
    Assertions.assertNotNull(answer);
  }

  @Test
  void doChatWithReport() {
    String chatId = UUID.randomUUID().toString();
    // 第一轮
    String message = "你好，我是Vizzini。我想进行一次中国西北环线的自驾，请你就旅游路线和城市给我一些建议。";
    PlayWhatApp.TravelReport travelReport = playWhatApp.doChatWithReport(message, chatId);
    Assertions.assertNotNull(travelReport);
  }

}