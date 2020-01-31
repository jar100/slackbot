package com.lq.slackbot.service;

import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.Restaurant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageEventServiceTest {

	@Test
	void name() {
		final double random = Math.random() * (Restaurant.values().length - 1);
		System.out.println(random);

	}

	@Test
	void name2() {
		final int random = (int) Math.round(Math.random() * (Restaurant.values().length - 1));
		String message = Restaurant.of(random).getName();

		System.out.println(message);
	}

	@Test
	void name3() {
		final boolean contains = "점심!".contains(MessageEventType.LUNCH.getLabel());
		System.out.println(contains);
	}
}