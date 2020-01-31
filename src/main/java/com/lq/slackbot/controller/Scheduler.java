package com.lq.slackbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class Scheduler {
	private static final String BASE_URL = System.getenv("SLACKBOT_BASE_URL");

	@Scheduled(cron="0 */10 *? * * *")
	public void check() {
		log.info("실행시간 : {}", LocalDateTime.now());
		final WebClient webClient = WebClient.create(BASE_URL);
		final Mono<String> stringMono = Objects.requireNonNull(webClient.get().uri("/init").exchange().block()).bodyToMono(String.class);
		log.info("nonSleep : {}",stringMono.block());
	}
}
