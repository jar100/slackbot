package com.lq.slackbot.domain;

public enum BirthdayImg {
	ONE("https://storage.googleapis.com/jjalbot-jjals/2018/12/14MhdDWm15/zzal.jpg");

	private String url;

	BirthdayImg(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}
