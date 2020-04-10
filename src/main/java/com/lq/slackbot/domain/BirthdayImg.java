package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://user-images.githubusercontent.com/39078045/78988597-56baf980-7b6c-11ea-8f91-196629cbecc4.jpg");

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
