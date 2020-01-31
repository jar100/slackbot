package com.lq.slackbot.domain;

public enum EventType {
	MESSAGE,APP_MENTION;

	public static EventType of(String name) {
		return valueOf(name.toUpperCase());
	}
}

