package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackRequest {
	private String token;
	private String team_id;
	private String api_app_id;
	private SlackEvent event;
	private String type;


	public String getChannel() {
		return event.getChannel();
	}

	public String eventType() {
		return event.getType().toUpperCase();
	}

	public String getUserId() {
		if (event == null) {
			return "";
		}
		return event.getUser();
	}
}
