package com.lq.slackbot.service;

import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.Restaurant;
import com.lq.slackbot.domain.SlackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageEventService {
	private MessageService messageService;
	private Map<String, List<Restaurant>> slackChannels = new HashMap<>();
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
		String restaurant = null;
		resetRestaurantByChannel(request);
		restaurant = findRestaurant(request.getChannel());
		messageService.sendMessageV3(request.getChannel(), restaurant);
		return restaurant;
	}

	private void resetRestaurantByChannel(final SlackRequest request) {
		if (request.getEvent().getText().contains("초기화")) {
			slackChannels.put(request.getChannel(), Restaurant.list());
			messageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
	}

	public void resetRestaurant() {
		slackChannels = new HashMap<>();
	}

	public String findRestaurant(String sessionKey) {
		final List<Restaurant> restaurantList = slackChannels.get(sessionKey);
		if (restaurantList == null || restaurantList.isEmpty()) {
			slackChannels.put(sessionKey,Restaurant.list());
		}
		return slackChannels.get(sessionKey).remove(0).getName();
	}
}
