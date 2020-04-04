package com.lq.slackbot.service;

import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.*;
import com.lq.slackbot.utils.JobUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class SchedulerService implements InitializingBean {
	private SchedulerFactoryBean schedulerFactoryBean;
	private ApplicationContext context;
	private ChannelRepository channelRepository;
	private ScheduleRepository scheduleRepository;

	public SchedulerService(final SchedulerFactoryBean schedulerFactoryBean, final ApplicationContext context, final ChannelRepository channelRepository, final ScheduleRepository scheduleRepository) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.context = context;
		this.channelRepository = channelRepository;
		this.scheduleRepository = scheduleRepository;
	}

	public boolean isJobExists(final JobKey jobKey) {
		final JobDetail jobDetail;
		try {
			jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
		} catch (SchedulerException e) {
			log.info("스캐줄 생성실패 : {}", e.getMessage(), e);
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


	public boolean addJob(Schedule schedule, Class<? extends Job> jobClass) {
		JobKey jobKey = null;
		JobDetail jobDetail;
		Trigger trigger;

		try {
			trigger = JobUtils.createCronTrigger(schedule);
			jobDetail = JobUtils.createJob(schedule, jobClass, context);
			jobKey = JobKey.jobKey(schedule.getId()+"", schedule.getChannel());
			Date dt = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			log.debug("Job with jobKey : {} scheduled successfully at date : {}", jobDetail.getKey(), dt);
			return true;
		} catch (SchedulerException | ParseException e) {
			log.error("error occurred while scheduling with jobKey : {} or , cronExpression : {}", jobKey, schedule.getCronExpression(), e);
		}
		return false;
	}


	public boolean deleteJobs(String scheduleId) {
		final Optional<Schedule> byId = scheduleRepository.findById(Long.valueOf(scheduleId));
		if (!byId.isPresent()) {
			return false;
		}
		final Schedule schedule = byId.get();
		JobKey jobKey = new JobKey(schedule.getIdToString(), schedule.getChannel());
		if (isJobExists(jobKey)) {
			deleteJob(jobKey);
			scheduleRepository.save(schedule.unUsed());
			return true;
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
		JobStatusResponse jobStatusResponse = new JobStatusResponse();
		List<JobResponse> jobs = new ArrayList<>();
		int numOfRunningJobs = 0;
		int numOfGroups = 0;
		int numOfAllJobs = 0;

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				numOfGroups++;
				jobs.addAll(createJobs(groupName));
			}
			numOfAllJobs = jobs.size();
		} catch (SchedulerException e) {
			log.error("[schedulerdebug] error while fetching all job info", e);
		}

		jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
		jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
		jobStatusResponse.setNumOfGroups(numOfGroups);
		jobStatusResponse.setJobs(jobs);
		return jobStatusResponse;
	}
	private Boolean validJob(JobRequest jobRequest) {
		JobKey jobKey = new JobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
		return !isJobExists(jobKey);
	}

	private Boolean validJob(Schedule schedule) {
		JobKey jobKey = new JobKey(schedule.getIdToString(), schedule.getChannel());
		return !isJobExists(jobKey);
	}

	public ResponseEntity<ApiResponse> addSchedule(final JobRequest jobRequest) {
		boolean isSuccess = false;
		Schedule save = null;
		if (!jobRequest.isUpdateJob() && Boolean.TRUE.equals(validJob(jobRequest))) {
			save = scheduleRepository.save(Schedule.builder()
					.channel(jobRequest.getJobGroup())
					.name(jobRequest.getJobName())
					.message(jobRequest.getJobDataMap())
					.cronExpression(jobRequest.getCronExpression())
					.used(true)
					.build());
			isSuccess = addJob(save, CronJob.class);
			if (!isSuccess) {
				scheduleRepository.save(save.unUsed());
			}
		}
		return apiResponse(isSuccess);
	}

	public ResponseEntity<ApiResponse> addSchedule(Schedule save) {
		boolean isSuccess = false;
		if (!save.isUpdateJob() && Boolean.TRUE.equals(validJob(save))) {
			save = scheduleRepository.save(save);
			isSuccess = addJob(save, CronJob.class);
			if (!isSuccess) {
				scheduleRepository.save(save.unUsed());
			}
		}
		return apiResponse(isSuccess);
	}

	private ResponseEntity<ApiResponse> apiResponse(final boolean isSuccess) {
		if (isSuccess) {
			return new ResponseEntity<>(new ApiResponse(true, "Job created successfully"), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse(false, "Job created false"), HttpStatus.CREATED);
	}

	public void deleteGroup(final String channelId) {
		try {
			final List<JobKey> collect = new ArrayList<>(schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(channelId)));
			schedulerFactoryBean.getScheduler().deleteJobs(collect);
		} catch (SchedulerException e) {
			log.error("스캐줄 삭제 실패 : {} ", e.getMessage(), e);
		}
	}

	public JobStatusResponse getAllJobGroup(final String channelId) {
		JobStatusResponse jobStatusResponse = new JobStatusResponse();
		List<JobResponse> jobs = new ArrayList<>();
		int numOfRunningJobs = 0;
		int numOfGroups = 0;
		int numOfAllJobs = 0;
		try {
			jobs.addAll(createJobs(channelId));
		} catch (SchedulerException e) {
			log.error("[schedulerdebug] error while fetching all job info", e);
		}
		jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
		jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
		jobStatusResponse.setNumOfGroups(numOfGroups);
		jobStatusResponse.setJobs(jobs);
		return jobStatusResponse;
	}

	private List<JobResponse> createJobs(String channelId) throws SchedulerException {
		final Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobResponse> jobs = new ArrayList<>();
		JobResponse jobResponse;
		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(channelId))) {
			List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
			JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
			Channel channel = channelRepository.findById(jobKey.getGroup()).orElse(new Channel());

			jobResponse = JobResponse.builder()
					.jobName(jobKey.getName())
					.groupName(jobKey.getGroup())
					.scheduleTime(String.valueOf(triggers.get(0).getStartTime()))
					.lastFiredTime(String.valueOf(triggers.get(0).getPreviousFireTime()))
					.nextFireTime(String.valueOf(triggers.get(0).getNextFireTime()))
					.message(String.valueOf(jobDetail.getJobDataMap().get("message")))
					.channelName(channel.getName())
					.build();
			jobs.add(jobResponse);
		}
		return jobs;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduleRepository.findAllByUsed(true).forEach(this::accept);
	}

	private void accept(Schedule schedule) {
		if (Boolean.TRUE.equals(validJob(schedule))) {
			addJob(schedule, CronJob.class);
		}
	}
}
