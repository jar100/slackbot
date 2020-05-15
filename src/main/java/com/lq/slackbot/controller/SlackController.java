package com.lq.slackbot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.Schedule;
import com.lq.slackbot.service.*;
import com.lq.slackbot.utils.SlackMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@Slf4j
public class SlackController {
	private final ObjectMapper objectMapper;
	private final MessageService service;
	private final MessageEventService messageEventService;
	private final SchedulerService schelduleService;
	private final SlackMessageHandler messageHandler;
	private final CoffeeService coffeeService;
	private final RestaurantService restaurantService;

	public SlackController(final ObjectMapper objectMapper, final MessageService service, final MessageEventService messageEventService, final SchedulerService schelduleService, final SlackMessageHandler messageHandler, final CoffeeService coffeeService, final RestaurantService restaurantService) {
		this.objectMapper = objectMapper;
		this.service = service;
		this.messageEventService = messageEventService;
		this.schelduleService = schelduleService;
		this.messageHandler = messageHandler;
		this.coffeeService = coffeeService;
		this.restaurantService = restaurantService;
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
		log.info("events request : {}", reqJson.toString());
		final SlackRequest slackRequest = objectMapper.convertValue(reqJson, SlackRequest.class);
		Gson gson = new Gson();
		log.info("slack request : {}", gson.toJson(slackRequest));

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
	@PostMapping(value = "/slack/modal", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<?> event(@RequestParam(name = "payload") String payload) throws JsonProcessingException, NoSuchAlgorithmException {
		log.info("payload : {}", payload);
		final Actions actions = objectMapper.readValue(payload, Actions.class);
		log.info("엑션스 : {}", actions);
		final SlackMessageEvent payload1 = objectMapper.readValue(payload, SlackMessageEvent.class);
		log.info("모달블럭 : {}", payload1);

		// 커피 Start
		if (actions.isCoffeeAction()) {
			return coffeeService.run(actions);
		}
		// 커피 End

		// 밥 Start
		if (actions.isRestaurantAction()) {
			return restaurantService.run(actions);
		}
		// 밥 End

		//todo  payload1 를 actions 와 통합 할 수 있을거같다.
		schedule(actions, payload1);
		return ResponseEntity.ok().build();
	}

	private void schedule(final Actions actions, final SlackMessageEvent payload1) {
		if (actions.getAction() != null) {
			if (actions.getAction().equals("scheduleDeleted_action")) {
				final String value = actions.getValue();
				schelduleService.deleteJobs(value);
				//todo 모달창에서 수정시 업데이트
				//삭제한 리스트 하나 삭제, 리스트 업데이트 반영
			}
			MessageService.sendMessageByModal(actions);
			return;
		}
		//모달창의 서브밋은 엑션이 이나디ㅏ.
		if (payload1.isViewSubmission()) {
			//스캐줄 생성
			final ResponseEntity<ApiResponse> apiResponseResponseEntity = schelduleService.addSchedule(Schedule.builder()
					.channel(payload1.getSubmissionChannelId())
					.name(payload1.getScheduleTitle())
					.cronExpression(payload1.getScheduleTimes())
					.message(payload1.getScheduleMessages())
					.used(true)
					.build());
			MessageService.sendMessageV3(payload1.getSubmissionChannelId(), apiResponseResponseEntity.getBody().getMessage());
		}
	}

	/**
	 * 응답을 먼저 반환해야 slack에서 재요청을 안보냄 그래서 비동기처리
	 * 여기도 리퀘스트 통일시켜야함... slackRequest == Actions
	 */
	@Async("threadPoolTaskExecutor")
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
