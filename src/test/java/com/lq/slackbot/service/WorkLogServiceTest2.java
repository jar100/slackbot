package com.lq.slackbot.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class WorkLogServiceTest2 {
	@Test
	void name() {
		final String format = LocalDate.now().toString();
		System.out.println(format);
	}
}