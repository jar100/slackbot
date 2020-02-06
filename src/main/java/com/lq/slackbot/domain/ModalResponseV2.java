package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModalResponseV2 {
	private String trigger_id;
	private String view;
}
