package com.lq.slackbot.domain;


import lombok.Data;

@Data
public class Payload {
	private String type;
	private SlackMessageEvent view;

}
