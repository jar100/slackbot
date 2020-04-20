package com.lq.slackbot.domain.schedule;

import com.lq.slackbot.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class ImgCronJob extends QuartzJobBean {
	private volatile Thread currThread;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String message = jobDataMap.getString("message");
		String img = jobDataMap.getString("img");

		JobKey jobKey = context.getJobDetail().getKey();

		currThread = Thread.currentThread();

		log.info("CronJob ended :: jobKey : {} - {}, message : {}", jobKey, currThread.getName(), message);
		MessageService.sendBirthdayMessage(jobKey.getGroup(), message, img);
	}
}
