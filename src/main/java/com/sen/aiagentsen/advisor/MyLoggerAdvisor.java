package com.sen.aiagentsen.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * 自定义日志 Advisor
 * 打印 info 级别日志、只输出单次用户提示词和 AI 回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 3;
    }

    /**
     * @param request 请求日志
     * @return
     */
    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI Request: {}", request.userText());
        return request;
    }

    /**
     * @param advisedResponse 响应日志
     */
    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("AI Response: {}", advisedResponse.response().getResult().getOutput().getText());
    }

    /**
     * @param advisedRequest 非流式请求
     * @param chain
     * @return
     */
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

        // 读取上下文
        //Object value = advisedResponse.adviseContext().get("key");

        // 在调用前处理请求
        advisedRequest = this.before(advisedRequest);
        // 调用链中的下一个环节
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        // 在调用后观察响应
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    /**
     * @param advisedRequest 流式请求
     * @param chain
     * @return
     */
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 在调用前处理请求
        advisedRequest = this.before(advisedRequest);
        // 调用链中的下一个环节，返回一个 Flux<AdvisedResponse>
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        // 在流式响应中观察每个响应
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }
}
