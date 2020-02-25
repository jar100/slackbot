package com.lq.slackbot.utils;

import com.google.gson.Gson;
import com.lq.slackbot.domain.JobRequest;
import com.lq.slackbot.domain.JobStatusResponse;
import com.lq.slackbot.domain.Message;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.service.MessageService;
import com.lq.slackbot.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.lq.slackbot.service.MessageService.createSelectBlock;

@Slf4j
@Component
public class SlackMessageHandler {
	private SchedulerService schedulerService;
	private Gson gson = new Gson();

	public SlackMessageHandler(final com.lq.slackbot.service.SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/**
 * 하는역할 슬랙에서 온 메세지를 핸들링하고 적절한 서비스에 분배해주는 역할
 * 앱맨션일경우 메세지를 핸들링해보자.
 *
 * @param slackRequest*/

	public String handling(final SlackRequest slackRequest) {
		final String text = slackRequest.getEvent().getText();
		if (text.contains("하이")) {
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("Hello World").build());
		} else if (text.contains("명령어")){
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("test")
					.blocks(createSelectBlock())
					.build());
		} else if (text.contains("schedule!")){
			// @adfqwef schedule! "클론식" "메세지ㅁㄴㅇㄹㅁㄴㄹㅇㅁㄴㅇㄹㄴㅇ ㄹ"
			final String[] split = text.split("\"");
			schedulerService.addSchedule(JobRequest.builder()
					.jobName(UUID.randomUUID().toString())
					.jobGroup(slackRequest.getChannel())
					.jobDataMap(split[3])
					.cronExpression(split[1])
					.build());
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("스캐줄 등록 완료")
					.build());
		} else if (text.contains("scheduleList!")) {
			final JobStatusResponse allJobGroup = schedulerService.getAllJobGroup(slackRequest.getChannel());

			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text(gson.toJson(allJobGroup))
					.build());
		}  else if (text.contains("deleteScheduleAll!")) {
			schedulerService.deleteGroup(slackRequest.getChannel());
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("스캐줄 삭제 완료")
					.build());
		} else if (text.contains("deleteSchedule!")) {
			final String[] split = text.split("\"");

			schedulerService.deleteJob(new JobKey(split[1],slackRequest.getChannel()));
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("스캐줄 삭제 완료")
					.build());
		}
		return "213";
	}
}
