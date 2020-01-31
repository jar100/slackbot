package com.lq.slackbot.service;

import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.Restaurant;
import com.lq.slackbot.domain.SlackRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageEventService {
	private MessageService messageService;

	public MessageEventService(final MessageService messageService) {
		this.messageService = messageService;
	}

	public void run(final SlackRequest request) {
		String message = null;

		final String text = request.getEvent().getText();
		if (StringUtils.isEmpty(text)) {
			return;
		}
		if (text.contains(MessageEventType.LUNCH.name())) {
			final int random = (int) Math.round(Math.random() * (Restaurant.values().length - 1));
			message = Restaurant.of(random).getName();
		}

		messageService.sendMessageV3(request.getChannel(),message);
	}
}
