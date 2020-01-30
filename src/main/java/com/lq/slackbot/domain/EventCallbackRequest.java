package com.lq.slackbot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventCallbackRequest {
	private Event event;

	public EventCallbackRequest(Event event) {
		this.event = event;
	}

	public String getType() {
		return event.getType();
	}

	public String getChannel() {
		return event.getChannel();
	}

	public String getUserId() {
		return event.getUser();
	}
}
