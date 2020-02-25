package com.lq.slackbot.domain;

import lombok.Data;

import java.util.List;

@Data
public class ChannelResponse {
	private String ok;
	private List<Channel> groups;

}
