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

	@Scheduled(cron="0 */15 * * * *")
	public void check() {
		log.info("실행시간 : {}", LocalDateTime.now());
		final WebClient webClient = WebClient.create("https://aqueous-plains-22889.herokuapp.com");
		final Mono<String> stringMono = Objects.requireNonNull(webClient.get().uri("/init").exchange().block()).bodyToMono(String.class);
		log.info("nonSleep : {}",stringMono.block());
	}
}
