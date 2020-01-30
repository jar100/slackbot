package com.lq.slackbot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Message {
	private String channel;
	private String text;

	public Message(String channel, String text) {
		this.channel = channel;
		this.text = text;
	}
}
