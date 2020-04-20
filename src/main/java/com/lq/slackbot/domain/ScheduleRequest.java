package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
	private long scheduleId;
	private String cronExpression;
	private String img;
	private String message;
}
