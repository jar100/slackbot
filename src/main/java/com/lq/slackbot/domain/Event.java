package com.lq.slackbot.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class Event {
	private String type;
	private String channel;
	private String user;

	public Event(String type, String channel, String user) {
		this.type = type;
		this.channel = channel;
		this.user = user;
	}
}
