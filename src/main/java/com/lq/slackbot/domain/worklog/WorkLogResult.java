package com.lq.slackbot.domain.worklog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogResult {
	private boolean result;
	private String userName;
	private String startWork;
	private String endWork;
}