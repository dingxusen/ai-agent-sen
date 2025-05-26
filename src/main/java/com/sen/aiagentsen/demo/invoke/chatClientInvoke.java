package com.sen.aiagentsen.demo.invoke;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

public class chatClientInvoke {
    // 方式1：使用构造器注入
    @Service
    public class ChatService {
        private final ChatClient chatClient;

        public ChatService(ChatClient.Builder builder) {
            this.chatClient = builder
                    .defaultSystem("你是恋爱顾问")
                    .build();
        }
    }

    private ChatModel chatModel;
    // 方式2：使用建造者模式
    ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultSystem("你是恋爱顾问")
            .build();


    // ChatClient支持多种响应格式

// 1. 返回 ChatResponse 对象（包含元数据如 token 使用量）
    ChatResponse chatResponse = chatClient.prompt()
            .user("Tell me a joke")
            .call()
            .chatResponse();

    // 2. 返回实体对象（自动将 AI 输出映射为 Java 对象）
// 2.1 返回单个实体
    record ActorFilms(String actor, List<String> movies) {}
    ActorFilms actorFilms = chatClient.prompt()
            .user("Generate the filmography for a random actor.")
            .call()
            .entity(ActorFilms.class);

    // 2.2 返回泛型集合
    List<ActorFilms> multipleActors = chatClient.prompt()
            .user("Generate filmography for Tom Hanks and Bill Murray.")
            .call()
            .entity(new ParameterizedTypeReference<List<ActorFilms>>() {});

    // 3. 流式返回（适用于打字机效果）
    Flux<String> streamResponse = chatClient.prompt()
            .user("Tell me a story")
            .stream()
            .content();

    // 也可以流式返回ChatResponse
    Flux<ChatResponse> streamWithMetadata = chatClient.prompt()
            .user("Tell me a story")
            .stream()
            .chatResponse();

}
