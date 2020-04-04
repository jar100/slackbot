package com.lq.slackbot.service;

import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.SlackRequest;
import com.lq.slackbot.domain.restaurant.Restaurant;
import com.lq.slackbot.domain.restaurant.RestaurantEnum;
import com.lq.slackbot.domain.restaurant.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestaurantService {
	private Map<String, List<RestaurantEnum>> slackChannels = new HashMap<>();
	private Map<String, Restaurant> tempRestaurant = new HashMap<>();
	private RestaurantRepository restaurantRepository;

	public RestaurantService(final RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}


	public String restaurantEvent(final SlackRequest request) {
		String restaurant = null;
		final List<Restaurant> restaurant1 = getRestaurant(request.getChannel());
		restaurant = findRestaurant(restaurant1);
		MessageService.sendMessageByRestaurant(request.getChannel(), restaurant);
		return restaurant;
	}

	private void resetRestaurantByChannel(final SlackRequest request) {
		if (request.getEvent().getText().contains("초기화")) {
			slackChannels.put(request.getChannel(), RestaurantEnum.list());
			MessageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
	}

	public String findRestaurant(List<Restaurant> restaurantList) {
		boolean isMatch = true;
		while (isMatch) {
			Collections.shuffle(restaurantList);
			final Restaurant restaurant1 = restaurantList.get(0);
			if (tempRestaurant.get(restaurant1.getChannel()) == null || !tempRestaurant.get(restaurant1.getChannel()).equals(restaurant1)) {
				isMatch = false;
			}
		}
		final Restaurant restaurant = restaurantList.remove(0);
		tempRestaurant.put(restaurant.getChannel(),restaurant);
		return restaurant.getName();
	}


	public ResponseEntity<?> run(final Actions actions) {
		if (actions.isRestaurantList()) {
			List<Restaurant> byChannelOrderByCountDesc = getRestaurant(actions.getChannelId());
			MessageService.sendMessageByModalV2(actions, byChannelOrderByCountDesc);
			return ResponseEntity.ok().build();
		}

		if (actions.isRetryRestaurant()) {
			final List<Restaurant> restaurant1 = getRestaurant(actions.getChannelId());
			String restaurant = findRestaurant(restaurant1);
			log.info("다시뽑기 : {}",restaurant);
			MessageService.updateByRestaurant(actions,restaurant);
		}

		if (actions.isSubmitRestaurant()) {
			final Restaurant restaurant = tempRestaurant.get(actions.getChannelId());

			log.info("결정 : {}", restaurant.getName());
			restaurant.increaseCount();
			restaurantRepository.save(restaurant);
			MessageService.updateResult(actions, restaurant.getName());
		}


		return ResponseEntity.ok().build();

	}

	public List<Restaurant> getRestaurant(String channel) {
		List<Restaurant> byChannelOrderByCountDesc = restaurantRepository.findByChannelOrderByCountDesc(channel);
		if (byChannelOrderByCountDesc.isEmpty()) {
			final List<Restaurant> collect = RestaurantEnum.list().stream().map(e -> e.ToRestaurant(channel)).collect(Collectors.toList());
			byChannelOrderByCountDesc = restaurantRepository.saveAll(collect);
		}
		return byChannelOrderByCountDesc;
	}
}
