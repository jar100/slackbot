package com.lq.slackbot.service;

import com.lq.slackbot.domain.*;
import com.lq.slackbot.utils.JobUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SchelduleService {
	private SchedulerFactoryBean schedulerFactoryBean;
	private ApplicationContext context;

	public SchelduleService(final SchedulerFactoryBean schedulerFactoryBean, final ApplicationContext context) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.context = context;
	}

	public boolean isJobExists(final JobKey jobKey) {
		final JobDetail jobDetail;
		try {
			jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
		return jobDetail != null;
	}

	public boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass) {
		JobKey jobKey = null;
		JobDetail jobDetail;
		Trigger trigger;

		try {
			trigger = JobUtils.createCronTrigger(jobRequest);
			jobDetail = JobUtils.createJob(jobRequest, jobClass, context);
			jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
			Date dt = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			log.debug("Job with jobKey : {} scheduled successfully at date : {}", jobDetail.getKey(), dt);
			return true;
		} catch (SchedulerException | ParseException e) {
			log.error("error occurred while scheduling with jobKey : {} or , cronExpression : {}", jobKey, jobRequest.getCronExpression(), e);
		}
		return false;
	}

	public void deleteJob(final JobKey jobKey) {
		try {
			schedulerFactoryBean.getScheduler().deleteJob(jobKey);
		} catch (SchedulerException e) {
			log.error("스캐줄 삭제 실패 : {} ", e.getMessage(), e);
		}
	}

	public void updateJob(final JobRequest jobRequest) {
		try {
			JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
			JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
			jobDetail.getJobDataMap().put("message", jobRequest.getJobDataMap());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public JobStatusResponse getAllJobs() {
		JobResponse jobResponse;
		JobStatusResponse jobStatusResponse = new JobStatusResponse();
		List<JobResponse> jobs = new ArrayList<>();
		int numOfRunningJobs = 0;
		int numOfGroups = 0;
		int numOfAllJobs = 0;

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				numOfGroups++;
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
					jobResponse = JobResponse.builder()
							.jobName(jobKey.getName())
							.groupName(jobKey.getGroup())
							.scheduleTime(triggers.get(0).getStartTime().toString())
							.lastFiredTime(triggers.get(0).getPreviousFireTime().toString())
							.nextFireTime(triggers.get(0).getNextFireTime().toString())
							.message(jobDetail.getJobDataMap().get("message").toString())
							.build();
					numOfAllJobs++;
					jobs.add(jobResponse);
				}
			}
		} catch (SchedulerException e) {
			log.error("[schedulerdebug] error while fetching all job info", e);
		}

		jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
		jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
		jobStatusResponse.setNumOfGroups(numOfGroups);
		jobStatusResponse.setJobs(jobs);
		return jobStatusResponse;
	}

	public ResponseEntity<?> addSchedule(final JobRequest jobRequest) {
		JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
		boolean isSuccess = false;
		if (!isJobExists(jobKey)) {
			if (jobRequest.getCronExpression() == null) {
				isSuccess = addJob(jobRequest, SimpleJob.class);
			} else {
				isSuccess = addJob(jobRequest, CronJob.class);
			}
		}
		return apiResponse(isSuccess);
	}

	private ResponseEntity<?> apiResponse(final boolean isSuccess) {
		if (isSuccess) {
			return new ResponseEntity<>(new ApiResponse(true, "Job created successfully"), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse(false, "Job created false"), HttpStatus.CREATED);
	}
}