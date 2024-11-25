package com.github.llm_redis_stream.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendMessageRequestDto {

    private String message;

}
