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
    String message = "你好，我是Vizzini。我想进行一次中国西北环线的自驾，请你就旅游路线和城市给我一些建议。";
    PlayWhatApp.TravelReport travelReport = playWhatApp.doChatWithReport(message, chatId);
    Assertions.assertNotNull(travelReport);
  }

  @Test
  void doChatWithLocalRag() {
    String chatId = UUID.randomUUID().toString();
    String message = "你好，我是Vizzini。我想进行一次陕西山西触摸历史的自驾，请你就旅游路线和城市给我一些建议。";
    String answer =  playWhatApp.doChatWithLocalRag(message, chatId);
    Assertions.assertNotNull(answer);
  }

  @Test
  void doChatWithRemoteRag() {
    String chatId = UUID.randomUUID().toString();
    String message = "你好，我是Vizzini。我想进行一次云南贵州遍览自然风光的自驾，请你就景点和美食给我一些建议。";
    String answer =  playWhatApp.doChatWithRemoteRag(message, chatId);
    Assertions.assertNotNull(answer);
  }
}