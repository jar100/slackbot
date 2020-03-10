package com.lq.slackbot.domain;

import com.lq.slackbot.utils.SystemUtils;
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
	private Container container;

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
		ScheduleTitle scheduleTitle;
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
	private static class ScheduleTitle {
		Value scheduleTitle;

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Value {
		String value;


	}
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class Container {
		String channel_id;
	}

	public boolean isViewSubmission() {
		return SystemUtils.VIEW_SUBMISSION.equals(this.type);
	}


	public String getScheduleMessages() {
		return this.view.state.values.scheduleMessage.scheduleMessage.value;
	}

	public String getScheduleTimes() {
		return this.view.state.values.scheduleTime.scheduleTime.value;
	}

	public String getScheduleTitle() {
		return this.view.state.values.scheduleTitle.scheduleTitle.value;
	}

	public String getChannelId() {
		return this.container.channel_id;
	}

	public String getSubmissionChannelId() {
		return this.view.callback_id.split("_")[1];
	}
}

