package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.JobRequest;
import com.lq.slackbot.service.CoffeeService;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SchedulerService;
import com.lq.slackbot.utils.SlackMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
@Slf4j
public class SlackController {
	private final ObjectMapper objectMapper;
	private final MessageService service;
	private final MessageEventService messageEventService;
	private final SchedulerService schelduleService;
	private final SlackMessageHandler messageHandler;
	private final CoffeeService coffeeService;

	public SlackController(final ObjectMapper objectMapper, final MessageService service, final MessageEventService messageEventService, final SchedulerService schelduleService, final SlackMessageHandler messageHandler, final CoffeeService coffeeService) {
		this.objectMapper = objectMapper;
		this.service = service;
		this.messageEventService = messageEventService;
		this.schelduleService = schelduleService;
		this.messageHandler = messageHandler;
		this.coffeeService = coffeeService;
	}

	@GetMapping("/init")
	public ResponseEntity<?> healsCheak() {
		return ResponseEntity.ok("ok");
	}


	@PostMapping("/test")
	public ResponseEntity<?> adfe() {
		return ResponseEntity.ok("ok");
	}

	//이벤트 리스너
	@PostMapping("/slack/events")
	public ResponseEntity<?> handleEvents(@RequestBody JsonNode reqJson) throws JsonProcessingException {
		log.info("events request : {}",reqJson.toString());
		final SlackRequest slackRequest = objectMapper.convertValue(reqJson, SlackRequest.class);
		log.info("slack request : {}", slackRequest);

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

	//모달이 아니라 동적 핸들링
	@PostMapping(value = "/slack/modal", produces="text/plain;charset=UTF-8")
	public ResponseEntity<?> event(@RequestParam(name = "payload") String payload ) throws JsonProcessingException, NoSuchAlgorithmException {
		log.info("payload : {}" , payload);
		final Actions actions = objectMapper.readValue(payload, Actions.class);
		log.info("엑션스 : {}",actions);
		final SlackMessageEvent payload1 = objectMapper.readValue(payload, SlackMessageEvent.class);
		log.info("모달블럭 : {}",payload1);
		log.info("get action value : {}",actions.getActions().get(0).getValue());

		// 커피 Run
		if (actions.isCoffeeAction()) {
			return coffeeService.run(actions);
		}
		// 커피 End

		//todo  payload1 를 actions 와 통합 할 수 있을거같다.
		if (actions.getAction() != null) {
			//메세지 팝업창 스캐줄버튼은 엑션이 아니다...
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
		}
		return ResponseEntity.ok().build();
	}

	/**
	 * 응답을 먼저 반환해야 slack에서 재요청을 안보냄 그래서 비동기처리
	 * 여기도 리퀘스트 통일시켜야함... slackRequest == Actions
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
