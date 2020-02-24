package com.lq.slackbot.domain;

import com.lq.slackbot.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class CronJob extends QuartzJobBean {
	private int MAX_SLEEP_IN_SECONDS = 5;
	private volatile Thread currThread;



	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		String message = jobDataMap.getString("message");
		JobKey jobKey = context.getJobDetail().getKey();

		currThread = Thread.currentThread();

		log.info("CronJob ended :: jobKey : {} - {}, message : {}", jobKey, currThread.getName(), message);
		MessageService.sendMessageV3("GTCF877M3",message);
	}
}