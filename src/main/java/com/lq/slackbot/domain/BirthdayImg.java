package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://files.slack.com/files-pri/T07SR86Q5-F0126GKMXJL/hbd__1_.jpg");

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
