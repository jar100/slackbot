package com.lq.slackbot.utils;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

class SlackMessageHandlerTest {

	@Test
	void 스트링파싱() {
		String test = "<@UTB3SKV71> 스캐줄!   \"0 0 0 0 0 0\" \"백경훈\"";
		final String[] split = test.split("\"");
		for (String s : split) {
			System.out.println(s);
			System.out.println(StringUtils.isBlank(s));
		}

	}

	@Test
	void 스트링파싱2() {
		String test = "<@UTB3SKV71> deleteSchedule!   \"adfaf\" ";
		final String[] split = test.split("\"");
		for (String s : split) {
			System.out.println(s);
			System.out.println(StringUtils.isBlank(s));
		}

	}
}