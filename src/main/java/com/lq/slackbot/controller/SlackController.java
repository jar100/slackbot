package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.EventCallbackRequest;
import com.lq.slackbot.domain.RequestType;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.service.SlackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@Slf4j
public class SlackController {
	private final ObjectMapper objectMapper;
	private final SlackService service;

	public SlackController(final ObjectMapper objectMapper, final SlackService service) {
		this.objectMapper = objectMapper;
		this.service = service;
	}

	@GetMapping("/batch")
	public ResponseEntity<?> batch() {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/init")
	public ResponseEntity<?> healsCheak() {
		return ResponseEntity.ok("ok");
	}



	@PostMapping("/slack/events")
	public ResponseEntity<?> handleEvents(@RequestBody JsonNode reqJson) throws JsonProcessingException {
		final SlackRequest slackRequest = objectMapper.convertValue(reqJson, SlackRequest.class);
		log.info("slack request : {}",slackRequest);
		log.info(reqJson.toString());

		switch (RequestType.of(reqJson.get("type").asText())) {
			case URL_VERIFICATION:
				return ResponseEntity.ok(reqJson.get("challenge"));
			case EVENT_CALLBACK:
				if ("message".equals(slackRequest.getEvent().getType()) && !slackRequest.getEvent().getUser().equals("UTB3SKV71")) {
					service.sendMessageV2(slackRequest);
				} else if ("app_mention".equals(slackRequest.getEvent().getType())) {
					service.sendMessage(jsonToDto(reqJson, EventCallbackRequest.class));
				}
				return ResponseEntity.ok().build();
			default:
				return ResponseEntity.badRequest().build();
		}

	}

	private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
		return objectMapper.treeToValue(json, type);
	}
}
