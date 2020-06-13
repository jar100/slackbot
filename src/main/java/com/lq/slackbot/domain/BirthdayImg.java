package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://user-images.githubusercontent.com/39078045/78988597-56baf980-7b6c-11ea-8f91-196629cbecc4.jpg"),
	TWO("https://user-images.githubusercontent.com/39078045/81413509-8582ba80-9180-11ea-9dee-b6586053e836.png"),
	PHOTO_1("https://user-images.githubusercontent.com/39078045/84565737-24b15800-ada6-11ea-8ebf-4f6588b4f2d6.png"),



	;

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
