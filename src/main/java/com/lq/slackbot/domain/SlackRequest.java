package com.lq.slackbot.domain;

import lombok.Data;

@Data
public class SlackRequest {
	private String token;
	private String team_id;
	private String api_app_id;
	private SlackEvent event;
	private String type;


	public String getChannel() {
		return event.getChannel();
	}
}
