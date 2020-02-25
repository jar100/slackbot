package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobResponse {
	private	String jobName;
	private	String groupName;
	private	String scheduleTime;
	private	String lastFiredTime;
	private	String nextFireTime;
	private String message;
	private String channelName;
}
