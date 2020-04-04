package com.lq.slackbot.service;

import com.lq.slackbot.utils.SystemUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

	@Test
	void name() {
		final WebClient build = WebClient.builder()
				.baseUrl(SystemUtils.GOOGLE_SPREADSHEETS_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
				.build();
		//https://docs.google.com/spreadsheets/d/1yHrBv9kbhjSJ0vrGxk_T6_ML9chOICOQrJi9FaY_PfY/edit?usp=sharing

		final ClientResponse block = build.get().uri("/1yHrBv9kbhjSJ0vrGxk_T6_ML9chOICOQrJi9FaY_PfY").exchange().block();
		final String stringMono = block.bodyToMono(String.class).block();
		System.out.println(stringMono);
	}


	@Test
	void findRestaurantList() {
		// 레스토랑을 불러온다.
	}

	@Test
	void 다시_돌리기() {
		//dailyList 를 사용해 다시 돌린다.(1스택) 바로 이전에 뽑은건 나오지 않는다.\
	}

	@Test
	void commit_restaurant() {
		//오늘 레스토랑을 선택한다.
		// 채널에 해당하는 레스토랑 리스트 에 선택한 음식의 카운트를 증가시킨다.

	}

	@Test
	void 통계_획인() {
		//해당하는 채널의 레스토랑의 순위를 뽑는다.

	}
}