package com.lq.slackbot.service;

import com.lq.slackbot.domain.SlackUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CoffeeServiceTest {
	@Mock
	private CoffeeService coffeeService;

	@Test
	void name() {
		List<SlackUser> userList = new ArrayList<>();
		userList.add(SlackUser.builder().slackId("123").build());
		userList.add(SlackUser.builder().slackId("1234").build());
//		assertThat(coffeeService.pickUser()).isEqualTo(SlackUser.builder().slackId("123").build());
	}
}