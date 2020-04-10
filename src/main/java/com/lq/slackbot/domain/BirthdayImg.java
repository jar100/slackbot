package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://user-images.githubusercontent.com/39078045/78988203-350d4280-7b6b-11ea-91ab-de8f96d7191e.jpg");

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
