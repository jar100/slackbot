package com.lq.slackbot.worklog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogRequest {
	private String type;
	private String auth_user_id;
	private String user_id;
	private String target_date;
	private String time;

}
