package com.lq.slackbot.service;

import com.lq.slackbot.domain.restaurant.RestaurantEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lq.slackbot.service.MessageService.joinUserCount;
import static org.assertj.core.api.Assertions.assertThat;

class MessageEventServiceTest {
	private MessageEventService messageEventService;

	@Test
	void name22() {
		List<String> aaa = new ArrayList<>();
		aaa.add("123");
		aaa.remove("123");
	}

	@Test
	void splitTest() {
		String s = joinUserCount(null);
		System.out.println(s);

		s = joinUserCount("백경훈");
		System.out.println(s);

		s = joinUserCount("백경훈, 노진산 ");
		System.out.println(s);
	}

	@Test
	void name() {
		final double random = Math.random() * (RestaurantEnum.values().length - 1);
		System.out.println(random);

	}

	@Test
	void 점심_출력_태스트() {
		final String s = messageEventService.findRestaurant("123");

		final String s1 = messageEventService.findRestaurant("123");
		final String s2 = messageEventService.findRestaurant("567");
		final String s3 = messageEventService.findRestaurant("345");

		System.out.printf("출력물 %s, %s, %s, %s",s,s1,s2,s3);
	}
	//	@Test
//	void stringParse() {
//		//given
//		String test = "밥! (123,ㅂㅈㄷ,5454,ㅊㅇㄹㄷㄱ,ㅁㅇㄴㅁㄹㄷㄱ,)";
//
//		//when
//		String[] text = messageEventService.parseMessage(test);
//
//		System.out.println(text);
//
//		//then
//		assertThat(text).isEqualTo("123,ㅂㅈㄷ,5454,ㅊㅇㄹㄷㄱ,ㅁㅇㄴㅁㄹㄷㄱ,");
//	}
}