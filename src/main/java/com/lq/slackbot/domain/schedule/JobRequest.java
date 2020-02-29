package com.lq.slackbot.domain.schedule;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
	private String jobName;
	private String jobGroup;
	private String cronExpression;
	private String jobDataMap;
	private String channelName;

}
