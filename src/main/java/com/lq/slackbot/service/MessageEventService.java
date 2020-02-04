package com.lq.slackbot.service;

import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.Restaurant;
import com.lq.slackbot.domain.SlackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MessageEventService {
	private MessageService messageService;

	@Autowired
	public MessageEventService(final MessageService messageService) {
		this.messageService = messageService;
	}

	public void run(final SlackRequest request) {
		String message = null;

		final String text = request.getEvent().getText();
		log.info("text : {}", text);
		if (StringUtils.isEmpty(text)) {
			log.error("택스트를 찾을 수 없음");
			return;
		}
		if (text.contains(MessageEventType.LUNCH.getLabel())) {
			final int random = (int) (Math.random() * (Restaurant.values().length - 1)) + 1;
			message = Restaurant.of(random).getName();
			messageService.sendMessageV3(request.getChannel(),message);
		}
		log.info(message);
	}
}
