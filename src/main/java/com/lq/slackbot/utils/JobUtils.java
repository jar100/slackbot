package com.lq.slackbot.utils;

import com.lq.slackbot.domain.schedule.CronJob;
import com.lq.slackbot.domain.schedule.JobRequest;
import com.lq.slackbot.domain.schedule.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.text.ParseException;

@Slf4j
public class JobUtils {

	public static JobDetail createJob(JobRequest jobRequest, Class<? extends Job> clazz, ApplicationContext context) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(CronJob.class);
		factoryBean.setDurability(false);
		factoryBean.setName(jobRequest.getJobName());
		factoryBean.setGroup(jobRequest.getJobGroup());
		factoryBean.setApplicationContext(context);
		if (jobRequest.getJobDataMap() != null) {
			factoryBean.setJobDataMap(createJobDataMap("message", jobRequest.getJobDataMap()));
		}
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	public static JobDetail createJob(Schedule schedule, Class<? extends Job> clazz, ApplicationContext context) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(CronJob.class);
		factoryBean.setDurability(false);
		factoryBean.setName(schedule.getId()+"");
		factoryBean.setGroup(schedule.getChannel());
		factoryBean.setApplicationContext(context);
		if (schedule.getMessage() != null) {
			factoryBean.setJobDataMap(createJobDataMap("message", schedule.getMessage()));
		}
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	private static JobDataMap createJobDataMap(String jobDataMap) {
		JobDataMap jobDataMap1 = new JobDataMap();
		jobDataMap1.put("message", jobDataMap);
		return jobDataMap1;
	}

	private static JobDataMap createJobDataMap(String key, String jobDataMap) {
		JobDataMap jobDataMap1 = new JobDataMap();
		jobDataMap1.put(key, jobDataMap);
		return jobDataMap1;
	}


	public static Trigger createCronTrigger(JobRequest jobRequest) throws ParseException {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setName(jobRequest.getJobName());
		factoryBean.setGroup(jobRequest.getJobGroup());
		factoryBean.setCronExpression(jobRequest.getCronExpression());
		factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	public static Trigger createCronTrigger(Schedule schedule) throws ParseException {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setName(schedule.getId() + "");
		factoryBean.setGroup(schedule.getChannel());
		factoryBean.setCronExpression(schedule.getCronExpression());
		factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
}
