package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.ApiResponse;
import com.lq.slackbot.domain.ChannelResponse;
import com.lq.slackbot.domain.JobRequest;
import com.lq.slackbot.domain.JobStatusResponse;
import com.lq.slackbot.service.SchelduleService;
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
	private SchelduleService scheduleService;

	public SchedulerController(final SchelduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	@RequestMapping(value = "/job", method = RequestMethod.POST)
	public ResponseEntity<?> addScheduleJob(@RequestBody JobRequest jobRequest) {
		return scheduleService.addSchedule(jobRequest);
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
		log.info("체널리스트 : {}", block);
		return block.toString();
	}
}
