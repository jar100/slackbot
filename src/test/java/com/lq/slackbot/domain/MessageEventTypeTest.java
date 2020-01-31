package com.lq.slackbot.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageEventTypeTest {

	@Test
	void findLunch() {
		final MessageEventType 점심 = MessageEventType.of("점심!");
		assertThat(점심).isEqualTo(MessageEventType.LUNCH);

	}
}