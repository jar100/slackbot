package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.domain.EventType;
import com.lq.slackbot.domain.RequestType;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SlackBotEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
				slackBotEvent(slackRequest);
				return ResponseEntity.ok().build();
			default:
				return ResponseEntity.badRequest().build();
		}

	}

	@PostMapping("/slack/modal")
	public ResponseEntity<?> event(@RequestParam Map<String,String> body ) throws JsonProcessingException {
		log.info("test modal");
		log.info(body.toString());
		final String payload = body.get("payload");

		log.info("페이로드 : {}",payload);
		final Actions actions = objectMapper.readValue(payload, Actions.class);
		log.info("엑션스 : {}",actions);
		service.sendMessageByModal(actions);
		return ResponseEntity.ok().build();
	}

	private void slackBotEvent(final SlackRequest slackRequest) throws JsonProcessingException {

		final EventType of = EventType.of(slackRequest.eventType());
		log.info("이벤트 타입 : {}",of);
		switch (of) {
			case MESSAGE:
				if (slackRequest.getEvent().isUser()) {
					messageEventService.run(slackRequest);
				}
				return;
			case APP_MENTION:
				service.sendMessageV2(slackRequest);
				return;
			default:
		}
	}

	private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
		return objectMapper.treeToValue(json, type);
	}
}
