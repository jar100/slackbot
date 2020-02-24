package com.lq.slackbot.domain;


import lombok.Data;

@Data
public class JobRequest {
	private String jobName;
	private String jobGroup;
	private String cronExpression;
	private String jobDataMap;

}
