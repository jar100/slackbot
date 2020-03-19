package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.JobRequest;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SchedulerService;
import com.lq.slackbot.utils.SlackMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class SlackController {
	private final ObjectMapper objectMapper;
	private final MessageService service;
	private final MessageEventService messageEventService;
	private final SchedulerService schelduleService;
	private final SlackMessageHandler messageHandler;

	public SlackController(final ObjectMapper objectMapper, final MessageService service, final MessageEventService messageEventService, final SchedulerService schelduleService, final SlackMessageHandler messageHandler) {
		this.objectMapper = objectMapper;
		this.service = service;
		this.messageEventService = messageEventService;
		this.schelduleService = schelduleService;
		this.messageHandler = messageHandler;
	}

	@GetMapping("/init")
	public ResponseEntity<?> healsCheak() {
		return ResponseEntity.ok("ok");
	}


	@PostMapping("/test")
	public ResponseEntity<?> adfe() {
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

	@PostMapping(value = "/slack/modal", produces="text/plain;charset=UTF-8")
	public ResponseEntity<?> event(@RequestParam(name = "payload") String payload ) throws JsonProcessingException {
		log.info("payload : {}" , payload);
		final Actions actions = objectMapper.readValue(payload, Actions.class);
		log.info("엑션스 : {}",actions);
		final SlackMessageEvent payload1 = objectMapper.readValue(payload, SlackMessageEvent.class);
		log.info("모달블럭 : {}",payload1);
		if (actions.getAction() != null) {
			//메세지 팝업창
			MessageService.sendMessageByModal(actions,payload1.getChannelId());
		} else if (payload1.isViewSubmission()) {
			//동작스케줄 창띄우기
			final ResponseEntity<ApiResponse> apiResponseResponseEntity = schelduleService.addSchedule(JobRequest.builder()
					.jobGroup(payload1.getSubmissionChannelId())
					.jobName(payload1.getScheduleTitle())
					.cronExpression(payload1.getScheduleTimes())
					.jobDataMap(payload1.getScheduleMessages())
					.build());
			MessageService.sendMessageV3(payload1.getSubmissionChannelId(),apiResponseResponseEntity.getBody().getMessage());
		} else if(actions.isCoffeeAction()) {
//			MessageService.update(actions);
			log.info("커피 이밴트 발생");
		}

		return ResponseEntity.ok().build();
	}

	/**
	 * 응답을 먼저 반환해야 slack에서 재요청을 안보냄 그래서 비동기처리
	 * */
	@Async
	public void slackBotEvent(final SlackRequest slackRequest) throws JsonProcessingException {
		final EventType of = EventType.of(slackRequest.eventType());
		switch (of) {
			case MESSAGE:
				if (slackRequest.getEvent().isUser()) {
					messageEventService.run(slackRequest);
				}
				return;
			case APP_MENTION:
				messageHandler.handling(slackRequest);
				return;
			default:
		}
	}

	private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
		return objectMapper.treeToValue(json, type);
	}
}
