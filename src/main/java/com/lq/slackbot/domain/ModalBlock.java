package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModalBlock {
	String type;
	String block_id;
	Content text;
	Accessory accessory;
	String view;

	@Data
	@Builder
	public static class Content {
		String text;
		String type;
	}

	@Data
	@Builder
	public static class Accessory {
		String type;
		List<Content> text;
		String action_id = "button-identifier";
	}

}
