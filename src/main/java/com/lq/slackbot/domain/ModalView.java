package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModalView {
	String id;
	String team_id;
	String type = "modal";
	Content title;
	Content submit;
	private List<ModalBlock> blocks;

	@Data
	@Builder
	public static class Content {
	 String type;
	 String text;
	}
}
