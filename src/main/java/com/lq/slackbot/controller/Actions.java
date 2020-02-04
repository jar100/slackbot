package com.lq.slackbot.controller;

import lombok.Data;

import java.util.List;

@Data
public class Actions {
	private List<Acution> actions;
	private String trigger_id;
	public Object getAction() {
		return actions.get(0).action_id;
	}


	@Data
	public static class Acution {
		private String action_id;
	}
}
