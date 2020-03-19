package com.lq.slackbot.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlackUser {
	private String slackId;
	private String name;


	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final SlackUser slackUser = (SlackUser) o;
		return Objects.equals(slackId, slackUser.slackId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(slackId);
	}
}
