package com.lq.slackbot.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actions {
	private List<Acution> actions;
	private String trigger_id;

	public Object getAction() {
		if (actions == null) {
			return null;
		}
		return actions.get(0).action_id;
	}


	@Data
	@AllArgsConstructor
	public static class Acution {
		private String action_id;
	}
}
