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
	String type;
	String callback_id;
	Content title;
	Content submit;
	Content close;
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
