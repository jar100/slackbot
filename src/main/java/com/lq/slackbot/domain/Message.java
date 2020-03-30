package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Message {
	private String channel;
	private String text;
	private String blocks;
	private String ts;

}
