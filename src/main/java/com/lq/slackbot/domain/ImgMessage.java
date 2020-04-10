package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImgMessage {
	private String channel;
	private String text;
	private String blocks;
	private String ts;
	private List<Attachment> attachments;
	private String user;


	@Data
	@Builder
	public static class Attachment {
		private String fallback;
		private String image_url;
		private long image_width;
		private long image_height;
		private long image_bytes;
		private String callback_id;
	}
}
