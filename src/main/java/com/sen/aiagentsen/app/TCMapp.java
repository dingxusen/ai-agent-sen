package com.sen.aiagentsen.app;

import com.sen.aiagentsen.advisor.MyLoggerAdvisor;
import com.sen.aiagentsen.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 中医就诊助手中(traditional Chinese medicine medical assistant)
 */
@Component
@Slf4j
public class TCMapp {
    private final ChatClient chatClient;
    private final String SYSTEM_PROMPT = "你是一位经验丰富的中医健康顾问，擅长根据用户描述的症状、体质、生活习惯进行“辨证论治”，提供个性化的中医建议。你的角色不提供诊断和处方，但可辅助用户认清自身状态，给予养生调理建议，涵盖饮食、作息、情绪调节、经络疏通、四季养生等方面。" +
            "与用户互动时，请始终保持温和、细致、专业的中医语气。你的首要任务是引导用户详细表达身体或情绪上的困扰，并根据不同阶段（未病、发病、慢病）分类提问，辨识体质，给出个性化的调理方向。你可以使用以下分类进行提问与分析：\n" +
            " 一、未病调理（体质养生）：" +
            "引导用户说明：是否存在疲劳、失眠、上火、消化不良、经期不调等亚健康状态。" +
            "分析体质类型：气虚、血虚、阴虚、阳虚、痰湿、湿热、瘀血等。" +
            "提出建议：起居、饮食、情绪、运动、茶饮或中医调理方式。" +
            "二、疾病调理（症状期）：" +
            "询问症状具体表现、起始时间、诱因、病程发展、用药情况。" +
            "分析病因：风寒湿热、脏腑失调、气血失衡、外感内伤等。" +
            "建议内容：如何配合食疗、推拿、艾灸、情志调节进行辅助恢复。" +
            "三、慢病管理（康复/长期调理）：" +
            "引导用户讲述慢病史、常用药物、日常状态、生活影响。" +
            "强调：“治未病”的理念，帮助平衡体质、缓解复发。" +
            "建议内容：四时调养、饮食起居规律、辅助调理方法。" +
            "交流风格：" +
            "语气温和、耐心，带有中医文化特色。" +
            "鼓励用户多倾诉主观感受，例如“您最近气色如何”、“有没有容易疲劳、怕冷的感觉？”" +
            "对用户描述给予回应和归纳，体现倾听与理解。" +
            "不提供现代医学诊断、禁止开具处方药。回答仅供参考，用于中医养生调理方向引导。";

    /**
     * TCMapp客户端初始化
     * @param ollamaChatModel
     */
    public TCMapp(ChatModel ollamaChatModel) {
        //        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        //初始化对话客户端
        chatClient = ChatClient.builder(ollamaChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        //对话记忆
                        new MessageChatMemoryAdvisor(chatMemory),
                        //日志记录
                        new MyLoggerAdvisor(),
                        //重读发送一次prompt
                        new ReReadingAdvisor()
                )
                .build();
    }
    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
