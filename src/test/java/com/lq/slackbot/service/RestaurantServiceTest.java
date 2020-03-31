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
}