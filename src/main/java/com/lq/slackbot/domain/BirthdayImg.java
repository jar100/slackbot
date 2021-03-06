package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://user-images.githubusercontent.com/39078045/78988597-56baf980-7b6c-11ea-8f91-196629cbecc4.jpg"),
	TWO("https://user-images.githubusercontent.com/39078045/81413509-8582ba80-9180-11ea-9dee-b6586053e836.png"),
	PHOTO_1("https://user-images.githubusercontent.com/39078045/84565737-24b15800-ada6-11ea-8ebf-4f6588b4f2d6.png"),
	PHOTO_2("https://user-images.githubusercontent.com/39078045/85374956-87f27580-b570-11ea-84d5-799b2b6d5785.png"),
	PHOTO_3("https://user-images.githubusercontent.com/39078045/85941912-e64c9900-b960-11ea-8f1f-9510304b82c0.png"),


	;

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
