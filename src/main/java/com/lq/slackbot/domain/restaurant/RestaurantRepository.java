package com.lq.slackbot.domain.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

	List<Restaurant> findByChannelOrderByCountDesc(String channel);

	@Query("select u from Restaurant u where u.channel = ?1 and u.isUse = ?2")
	List<Restaurant> findByChannelAndIsUedOrderByCountDesc(String channel, boolean isUse);
}
