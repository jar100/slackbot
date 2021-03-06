package com.lq.slackbot.domain;

import java.util.Arrays;

public enum MessageEventType {
	LUNCH("밥!"),
	ORDER("주문검색!"),
	NONE("없음"),
	WORK_START("출근!"),
	WORK_END("퇴근!");

	private String label;


	MessageEventType(String name) {
		this.label =  name;
	}

	public static MessageEventType of(String name) {
		return Arrays.stream(values()).filter(o->o.label.equals(name)).findFirst().orElse(NONE);
	}
	public String getLabel() {
		return this.label;
	}
}
