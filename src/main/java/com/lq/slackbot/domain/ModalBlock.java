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
public class ModalBlock {
	String type;
	String block_id;
	Content text;
	Content label;
	List<Elements> elements;
	Elements accessory;
	Elements element;
	Boolean optional;

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Content {
		String text;
		String type;
		Boolean emoji;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Elements {
		String type;
		Content text;
		String action_id;
		String value;
		Boolean multiline;
		String style;
	}

	public String getMessageText() {
		return this.text.text;
	}
}
