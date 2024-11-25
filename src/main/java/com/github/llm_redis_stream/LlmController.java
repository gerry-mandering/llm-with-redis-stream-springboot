package com.github.llm_redis_stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

import com.github.llm_redis_stream.dto.SendMessageRequestDto;
import com.github.llm_redis_stream.dto.SendMessageResponseDto;

@RestController
@RequiredArgsConstructor
public class LlmController {

    private final LlmService llmService;

    @PostMapping("/messages")
    public Mono<ResponseEntity<SendMessageResponseDto>> sendMessage(
            @RequestBody SendMessageRequestDto requestDto) {
        SendMessageResponseDto responseDto = llmService.sendMessage(requestDto);
        return Mono.just(ResponseEntity.ok(responseDto));
    }

    @GetMapping(value = "/messages/{streamMessageId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamMessage(
            @PathVariable("streamMessageId") String streamMessageId) {
        return llmService.streamMessage(streamMessageId)
                .map(data -> ServerSentEvent.builder(data).build());
    }

}
