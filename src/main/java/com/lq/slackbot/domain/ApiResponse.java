package com.lq.slackbot.domain;


import lombok.Data;

@Data
public class ApiResponse {
	private boolean ok;
	private String message;

	public ApiResponse(final boolean b, final String message) {
		this.ok = b;
		this.message = message;
	}
}
