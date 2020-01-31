package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.EventCallbackRequest;
import com.lq.slackbot.domain.EventType;
import com.lq.slackbot.domain.RequestType;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SlackBotEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SlackController {
	private final ObjectMapper objectMapper;
	private final MessageService service;
	private final SlackBotEventService eventService;
	private final MessageEventService messageEventService;

	public SlackController(final ObjectMapper objectMapper, final MessageService service, final SlackBotEventService eventService, final MessageEventService messageEventService) {
		this.objectMapper = objectMapper;
		this.service = service;
		this.eventService = eventService;
		this.messageEventService = messageEventService;
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
		log.info("slack request : {}", slackRequest);
		log.info(reqJson.toString());

		switch (RequestType.of(reqJson.get("type").asText())) {
			case URL_VERIFICATION:
				return ResponseEntity.ok(reqJson.get("challenge"));
			case EVENT_CALLBACK:
				slackBotEvent(reqJson, slackRequest);
				return ResponseEntity.ok().build();
			default:
				return ResponseEntity.badRequest().build();
		}

	}

	private void slackBotEvent(@RequestBody final JsonNode reqJson, final SlackRequest slackRequest) throws JsonProcessingException {
		switch (EventType.of(slackRequest.eventType())) {
			case MESSAGE:
				if (!slackRequest.getEvent().isBot()) {
					messageEventService.run(slackRequest);
				}
				return;
			case APP_MENTION:
				service.sendMessage(jsonToDto(reqJson, EventCallbackRequest.class));
				return;
			default:
		}
	}

	private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
		return objectMapper.treeToValue(json, type);
	}
}
