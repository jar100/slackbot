package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModalView {
//	String id;
//	String team_id;
	String type;
	Content title;
	Content submit;
	Content close;
//	private List<ModalBlock> blocks;
	private String blocks;

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Content {
	 String type;
	 String text;
	 boolean emoji;
	}
}
