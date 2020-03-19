package com.lq.slackbot.domain;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class SlackEvent {
	private String client_msg_id;
	private String bot_id;
	private String type;
	private String subtype;
	private String text;
	private String user;
	private String ts;
	private String team;
	private String channel;
	private String event_ts;
	private String channel_type;

	public boolean isUser() {
		return StringUtils.isEmpty(bot_id);
	}
}
