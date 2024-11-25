package com.github.llm_redis_stream;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.llm_redis_stream.dto.ClaudeRequestBody;
import com.github.llm_redis_stream.dto.SendMessageRequestDto;
import com.github.llm_redis_stream.dto.SendMessageResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final WebClient claudeWebClient;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;

    public SendMessageResponseDto sendMessage(SendMessageRequestDto requestDto) {
        String streamMessageId = UUID.randomUUID().toString();

        ClaudeRequestBody claudeRequestBody = createClaudeRequestBody(requestDto);

        claudeWebClient.post()
                .bodyValue(claudeRequestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseJson)
                .filter(json -> json != null)
                .takeUntil(node -> "message_stop".equals(node.path("type").asText()))
                .filter(this::isContentDelta)
                .map(this::extractDeltaContent)
                .flatMap(content -> reactiveRedisTemplate.opsForStream()
                        .add("message-stream:" + streamMessageId,
                                Collections.singletonMap("content", content)))
                .doOnComplete(() -> publishEndMessage(streamMessageId))
                .doOnError(error -> {
                    log.error("Error sending message", error);
                    clearStream(streamMessageId);
                })
                .subscribe();

        return SendMessageResponseDto.builder()
                .streamMessageId(streamMessageId)
                .build();
    }

    private ClaudeRequestBody createClaudeRequestBody(SendMessageRequestDto requestDto) {
        ClaudeRequestBody.Message message = ClaudeRequestBody.Message.builder()
                .role("user")
                .content(requestDto.getMessage())
                .build();

        return ClaudeRequestBody.builder()
                .model("claude-3-5-sonnet-20241022")
                .stream(true)
                .maxTokens(1024)
                .messages(Collections.singletonList(message))
                .build();
    }

    public Flux<String> streamMessage(String streamMessageId) {
        return streamReceiver.receive(StreamOffset.fromStart("message-stream:" + streamMessageId))
                .map(record -> record.getValue().get("content"))
                .takeUntil(content -> "[DONE]".equals(content))
                .timeout(Duration.ofMinutes(1))
                .doFinally(signalType -> clearStream(streamMessageId));
    }

    private JsonNode parseJson(String line) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(line);
        } catch (JsonMappingException e) {
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private boolean isContentDelta(JsonNode node) {
        return "content_block_delta".equals(node.path("type").asText());
    }

    private String extractDeltaContent(JsonNode node) {
        return node.path("delta")
                .path("text")
                .asText("");
    }

    private void publishEndMessage(String streamMessageId) {
        reactiveRedisTemplate.opsForStream()
                .add("message-stream:" + streamMessageId,
                        Collections.singletonMap("content", "[DONE]"))
                .subscribe();
    }

    private void clearStream(String streamMessageId) {
        reactiveRedisTemplate
                .delete("message-stream:" + streamMessageId)
                .subscribe();
    }

}
