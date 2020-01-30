package com.lq.slackbot.domain;

import lombok.Data;

@Data
public class SlackEvent {
	private String client_msg_id;
	private String type;
	private String text;
	private String user;
	private String ts;
	private String team;
	private String channel;
	private String event_ts;
	private String channel_type;


}
