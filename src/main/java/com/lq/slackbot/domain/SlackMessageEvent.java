package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlackMessageEvent {
	private View view;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class View {
		private String type;
		State state;
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
