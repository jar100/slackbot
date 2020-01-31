package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlackControllerTest {
	private MessageEventService messageEventService = new MessageEventService(new MessageService(new ObjectMapper()));
	@Test
	void lunchMessage() {
		final SlackRequest request = SlackRequest.builder().build();

		// 메세지에 "!점심" 포함하면 점심출력
		messageEventService.run(request);

	}
}