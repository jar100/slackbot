package com.lq.slackbot.service;

import com.lq.slackbot.domain.Restaurant;
import com.lq.slackbot.domain.SlackRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
	private Map<String, List<Restaurant>> slackChannels = new HashMap<>();

	public void save() {

	}

	public String restaurantEvent(final SlackRequest request) {
			String restaurant = null;
			resetRestaurantByChannel(request);
			restaurant = findRestaurant(request.getChannel());
			MessageService.sendMessageV3(request.getChannel(), restaurant);
			return restaurant;
	}

	private void resetRestaurantByChannel(final SlackRequest request) {
		if (request.getEvent().getText().contains("초기화")) {
			slackChannels.put(request.getChannel(), Restaurant.list());
			MessageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
	}

	public String findRestaurant(String sessionKey) {
		final List<Restaurant> restaurantList = slackChannels.get(sessionKey);
		if (restaurantList == null || restaurantList.isEmpty()) {
			slackChannels.put(sessionKey,Restaurant.list());
		}
		return slackChannels.get(sessionKey).remove(0).getName();
	}



}
