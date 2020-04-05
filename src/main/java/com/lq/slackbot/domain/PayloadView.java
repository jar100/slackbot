package com.lq.slackbot.domain;

import lombok.Data;

@Data
public class PayloadView {
	private String previous_view_id;
	private String root_view_id;
	private String external_id;
}
