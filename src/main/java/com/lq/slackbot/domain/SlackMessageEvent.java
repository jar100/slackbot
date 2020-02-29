package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlackMessageEvent {
	private String type;
	private View view;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class View {
		private String type;
		private State state;
		private String callback_id;
		private List<ModalBlock> blocks;

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class State {
		private Values values;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Values {
		ScheduleMessages scheduleMessage;
		ScheduleTimes scheduleTime;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ScheduleMessages {
		Value scheduleMessage;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ScheduleTimes {
		Value scheduleTime;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Value {
		String value;
	}
}
