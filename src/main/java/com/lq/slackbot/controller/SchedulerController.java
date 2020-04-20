package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.JobRequest;
import com.lq.slackbot.domain.schedule.JobStatusResponse;
import com.lq.slackbot.domain.schedule.Schedule;
import com.lq.slackbot.service.ChannelService;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SchedulerService;
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

	//	Job 추가 : POST /scheduler/job
//	모든 등록된 Job 조회 : GET /scheduler/jobs
//	Job 삭제 : DELETE /scheduler/job
//	Job 멈춤 : PUT /scheduler/job/pause
//	Job 재시작 : PUT /scheduler/job/resume
//
	private SchedulerService scheduleService;
	private ChannelService channelService;

	public SchedulerController(final SchedulerService scheduleService, final ChannelService channelService) {
		this.scheduleService = scheduleService;
		this.channelService = channelService;
	}

	@RequestMapping(value = "/job", method = RequestMethod.POST)
	public ResponseEntity<?> addScheduleJob(@RequestBody JobRequest jobRequest) {
		return scheduleService.addSchedule(jobRequest);
	}

	@PostMapping(value = "/job2")
	public ResponseEntity<?> addScheduleJobV2(@RequestBody Schedule schedule) {
		return scheduleService.addSchedule(schedule);
	}

	@GetMapping("/birthday")
	public void birthday() {
		log.info("실행시간 webHook: {}", LocalDateTime.now());
		//on_lqt
		String channel = "GHCQ4856Y";
		//경연님아이
		String name = "UN09A698S";
		MessageService.sendBirthdayMessage(channel, "<!here>\n" +
				":birthday-hangul::kiss::car::sunny::han-yo: \n 생일 축하합니다~ 생일 축하합니다~:tada:\n" +
				"사랑하는 :heartpulse::heartbeat:" +
				"<@" +name + ">"+
				":heartbeat::heartpulse:\n" +
				"생일 축하합니다~~~:clapping:  와아아아아아ㅏㅏㅏ",BirthdayImg.ONE.getUrl());
	}

	@PutMapping("/updateSchedule")
	public boolean updateSchedule(@RequestBody ScheduleRequest scheduleRequest) {
		log.info("request : {}", scheduleRequest);
		final Schedule schedule = scheduleService.getSchedule(scheduleRequest.getScheduleId());
		schedule.updateData(scheduleRequest);
		return scheduleService.updateJob(schedule);
	}

	@RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
	public ResponseEntity<?> deleteScheduleJob(@RequestBody JobRequest jobRequest) {
		JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
		if (scheduleService.isJobExists(jobKey)) {
			scheduleService.deleteJob(jobKey);
			return new ResponseEntity<>(new ApiResponse(true, "Job deleted"),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(true, "Job not found"), HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/deleteGroups", method = RequestMethod.GET)
	public ResponseEntity<?> deleteAllScheduleGroup(@RequestBody String channelId) {
		try {
			scheduleService.deleteGroup(channelId);
			return new ResponseEntity<>(new ApiResponse(true, "Job deleted"),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("exception : {}", e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse(true, "Job not found"), HttpStatus.BAD_REQUEST);
		}


	}

	@GetMapping("/allJob")
	public JobStatusResponse allJob() {
		return scheduleService.getAllJobs();
	}

	@GetMapping("/channelList")
	public String channelList() {
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config ->
						config.customCodecs().encoder(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON))
				).build();
		final WebClient webClient = WebClient.builder()
				.exchangeStrategies(strategies)
				.baseUrl(SystemUtils.BASE_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, SystemUtils.TOKEN)
				.build();

		final ChannelResponse block = Objects.requireNonNull(webClient.get().uri(SystemUtils.CHANNEL_LIST).exchange().block()).bodyToMono(ChannelResponse.class).block();
		assert block != null;
		log.info("체널리스트 : {}", block.getGroups());
		channelService.Save(block.getGroups());
		return block.toString();
	}

	@GetMapping("/deleteAll")
	public String deleteAll() {
		channelService.deleteAll();
		return "ok";
	}
}
