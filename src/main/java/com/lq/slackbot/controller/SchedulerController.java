package com.lq.slackbot.controller;

import com.lq.slackbot.domain.*;
import com.lq.slackbot.service.SchelduleService;
import org.quartz.JobKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
