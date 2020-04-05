package com.lq.slackbot.domain;

public class ModalViewUpdate extends ModalView {
	private String viewId;

	public ModalView updateViewId(String viewId) {
		this.viewId = viewId;
		return this;
	}
}
