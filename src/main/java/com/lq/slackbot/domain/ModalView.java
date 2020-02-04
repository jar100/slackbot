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
	Content close;
	//private List<ModalBlock> blocks;
	private String blocks;

	@Data
	@Builder
	public static class Content {
	 String type;
	 String text;
	 boolean emoji;
	}
}
