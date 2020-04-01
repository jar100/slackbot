package com.lq.slackbot.controller;

import com.lq.slackbot.domain.*;
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
	private SlackUser user;
	private List<Acution> actions;
	private String trigger_id;
	private ActionData message;
	private Channel channel;

	public Object getAction() {
		if (actions == null) {
			return null;
		}
		return actions.get(0).action_id;
	}

	public boolean isStartUser() {
		if (actions.isEmpty()) {
			return false;
		}
		return user.equals(SlackUser.builder().id(actions.get(0).block_id).build());
	}


	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Acution {
		private String action_id;
		private String block_id;
		private String value;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ActionData {
		private String type;
		private String text;
		private String user;
		private String ts;
		private List<ModalBlock> blocks;
	}

	public boolean isCoffeeMemberIn() {
		return "coffee_into".equals(this.actions.get(0).value);
	}

	public boolean isCoffeeDoAction() {
		return "coffee_action".equals(this.actions.get(0).action_id);
	}

	public boolean isCoffeeAction() {
		if (this.actions == null) {
			return false;
		}

		return isCoffeeMemberIn() || isCoffeeDoAction();
	}

	public String getUpdateCoffeeMessage() {
		if (this.message.blocks.size() > 2) {
			return this.message.blocks.get(1).getMessageText();
		}
		return "";
	}
}
