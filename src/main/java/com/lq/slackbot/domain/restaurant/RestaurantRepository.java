package com.lq.slackbot.domain.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

	List<Restaurant> findByChannelOrderByCountDesc(String channel);
}
