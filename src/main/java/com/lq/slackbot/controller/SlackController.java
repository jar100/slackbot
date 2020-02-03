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

	@PostMapping("/slack/modal")
	public ResponseEntity<?> event(@RequestBody JsonNode request) {
		log.info("test modal");
		log.info(request.toString());
		return ResponseEntity.ok().build();
	}

	private void slackBotEvent(@RequestBody final JsonNode reqJson, final SlackRequest slackRequest) throws JsonProcessingException {
		String test = "{\n" +
				"\t\t\"type\": \"section\",\n" +
				"\t\t\"text\": {\n" +
				"\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\"text\": \":wave: Hey David!\\n\\nWe'd love to hear from you how we can make this place the best place you’ve ever worked.\",\n" +
				"\t\t\t\"emoji\": true\n" +
				"\t\t}\n" +
				"\t},\n" +
				"\t{\n" +
				"\t\t\"type\": \"divider\"\n" +
				"\t},\n" +
				"\t{\n" +
				"\t\t\"type\": \"input\",\n" +
				"\t\t\"label\": {\n" +
				"\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\"text\": \"You enjoy working here at Pistachio & Co\",\n" +
				"\t\t\t\"emoji\": true\n" +
				"\t\t},\n" +
				"\t\t\"element\": {\n" +
				"\t\t\t\"type\": \"radio_buttons\",\n" +
				"\t\t\t\"options\": [\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \"Stronlgy agree\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"1\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \"Agree\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"2\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \"Neither agree nor disagree\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"3\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \"Disagree\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"4\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \"Strongly disagree\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"5\"\n" +
				"\t\t\t\t}\n" +
				"\t\t\t]\n" +
				"\t\t}\n" +
				"\t},\n" +
				"\t{\n" +
				"\t\t\"type\": \"input\",\n" +
				"\t\t\"label\": {\n" +
				"\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\"text\": \"What do you want for our team weekly lunch?\",\n" +
				"\t\t\t\"emoji\": true\n" +
				"\t\t},\n" +
				"\t\t\"element\": {\n" +
				"\t\t\t\"type\": \"multi_static_select\",\n" +
				"\t\t\t\"placeholder\": {\n" +
				"\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\"text\": \"Select your favorites\",\n" +
				"\t\t\t\t\"emoji\": true\n" +
				"\t\t\t},\n" +
				"\t\t\t\"options\": [\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":pizza: Pizza\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-0\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":fried_shrimp: Thai food\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-1\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":desert_island: Hawaiian\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-2\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":meat_on_bone: Texas BBQ\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-3\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":hamburger: Burger\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-4\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":taco: Tacos\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-5\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":green_salad: Salad\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-6\"\n" +
				"\t\t\t\t},\n" +
				"\t\t\t\t{\n" +
				"\t\t\t\t\t\"text\": {\n" +
				"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\t\t\t\"text\": \":stew: Indian\",\n" +
				"\t\t\t\t\t\t\"emoji\": true\n" +
				"\t\t\t\t\t},\n" +
				"\t\t\t\t\t\"value\": \"value-7\"\n" +
				"\t\t\t\t}\n" +
				"\t\t\t]\n" +
				"\t\t}\n" +
				"\t},\n" +
				"\t{\n" +
				"\t\t\"type\": \"input\",\n" +
				"\t\t\"label\": {\n" +
				"\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\"text\": \"What can we do to improve your experience working here?\",\n" +
				"\t\t\t\"emoji\": true\n" +
				"\t\t},\n" +
				"\t\t\"element\": {\n" +
				"\t\t\t\"type\": \"plain_text_input\",\n" +
				"\t\t\t\"multiline\": true\n" +
				"\t\t}\n" +
				"\t},\n" +
				"\t{\n" +
				"\t\t\"type\": \"input\",\n" +
				"\t\t\"label\": {\n" +
				"\t\t\t\"type\": \"plain_text\",\n" +
				"\t\t\t\"text\": \"Anything else you want to tell us?\",\n" +
				"\t\t\t\"emoji\": true\n" +
				"\t\t},\n" +
				"\t\t\"element\": {\n" +
				"\t\t\t\"type\": \"plain_text_input\",\n" +
				"\t\t\t\"multiline\": true\n" +
				"\t\t},\n" +
				"\t\t\"optional\": true\n" +
				"\t}";



		switch (EventType.of(slackRequest.eventType())) {
			case MESSAGE:
				if (slackRequest.getEvent().isUser()) {
					messageEventService.run(slackRequest);
				}
				return;
			case APP_MENTION:
				if (slackRequest.getEvent().getText().contains("주문")) {
					service.sendMessageV3(jsonToDto(reqJson, EventCallbackRequest.class), test);
				}
				service.sendMessage(jsonToDto(reqJson, EventCallbackRequest.class));
				return;
			default:
		}
	}

	private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
		return objectMapper.treeToValue(json, type);
	}
}
