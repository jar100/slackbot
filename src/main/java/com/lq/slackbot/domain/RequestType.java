package com.lq.slackbot.domain;

public enum RequestType {
	URL_VERIFICATION,
	EVENT_CALLBACK;

	public static RequestType of(String name) {
		return valueOf(name.toUpperCase());
	}
}
