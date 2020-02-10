package com.lq.slackbot.service;

import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.Restaurant;
import com.lq.slackbot.domain.SlackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MessageEventService {
	private MessageService messageService;
	private List<Restaurant> restaurantList = Restaurant.list();

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
			message = restaurantEvent(request);
		}
		log.info(message);
	}

	private String restaurantEvent(final SlackRequest request) {
		if (restaurantList.isEmpty() || request.getEvent().getText().contains("초기화")) {
			resetRestaurant();
			messageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
		Collections.shuffle(restaurantList);
		final String restaurant = restaurantList.remove(0).getName();

		messageService.sendMessageV3(request.getChannel(),restaurant);
		return restaurant;
	}

	public void resetRestaurant() {
		restaurantList = Restaurant.list();
	}
}
