package com.github.llm_redis_stream.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SendMessageResponseDto {

    private String streamMessageId;

    @Builder
    public SendMessageResponseDto(String streamMessageId) {
        this.streamMessageId = streamMessageId;
    }

}
