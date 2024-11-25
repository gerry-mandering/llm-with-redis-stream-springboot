package com.github.llm_redis_stream.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ClaudeRequestBody {

    private String model;
    private boolean stream;
    @JsonProperty("max_tokens")
    private int maxTokens;
    private List<Message> messages;

    @Builder
    public ClaudeRequestBody(String model, boolean stream, int maxTokens, List<Message> messages) {
        this.model = model;
        this.stream = stream;
        this.maxTokens = maxTokens;
        this.messages = messages;
    }

    @Getter
    public static class Message {

        private String role;
        private String content;

        @Builder
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

}
