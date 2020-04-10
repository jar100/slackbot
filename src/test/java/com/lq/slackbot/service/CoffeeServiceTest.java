package com.lq.slackbot.service;

import com.lq.slackbot.domain.SlackUser;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


class CoffeeServiceTest {
	@Mock
	private CoffeeService coffeeService;

	@Test
	void name() {
		List<SlackUser> userList = new ArrayList<>();
		userList.add(SlackUser.builder().id("123").build());
		userList.add(SlackUser.builder().id("1234").build());
//		assertThat(coffeeService.pickUser()).isEqualTo(SlackUser.builder().slackId("123").build());
	}

	@Test
	void coffeePick() throws NoSuchAlgorithmException {
		String users = "@gh.baek,@wanki.lee,@hancheon.park,@Joongyeop Ji,@seuk.jung,@jihun.lee";
		final String[] split = users.split(",");
		Random rand = SecureRandom.getInstanceStrong();
		int one = 0;
		int zero = 0;
		int two = 0;
		String[] userList = {"a", "b", "c"};
		final List<String> userList1 = Arrays.asList(split);
		System.out.println(userList1);
		System.out.println("////////");
		for (int i = 0; i < 10; i++) {
			String user = pickUser(userList1);
			System.out.println(user);
		}
		System.out.println(String.format("a : %d, b : %d, c : %d",zero,one,two));
	}

	public String pickUser(List<String> userList) throws NoSuchAlgorithmException {
		Random rand = SecureRandom.getInstanceStrong();
		final int i = rand.nextInt(userList.size());
		return userList.get(i);
	}

	@Test
	void listCheck() {
		final List<Object> objects = Arrays.asList();
		System.out.println(objects.isEmpty());

	}
}
