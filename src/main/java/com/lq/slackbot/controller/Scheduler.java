package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class Scheduler {
	private static final String BASE_URL = System.getenv("SLACKBOT_BASE_URL");
	private static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");
	private static final String WEB_HOOK_URL = System.getenv("WEB_HOOK_URL");

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public Scheduler(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}


	@Scheduled(cron = "0 */10 *? * * *")
	public void check() {
		log.info("실행시간 nonSleep : {}", LocalDateTime.now());
		final WebClient webClient = WebClient.create(BASE_URL);
		final Mono<String> stringMono = Objects.requireNonNull(webClient.get().uri("/init").exchange().block()).bodyToMono(String.class);
		log.info("nonSleep : {}", stringMono.block());
	}


	@Scheduled(cron = "0 0 15 * * THU")
//	@Scheduled(cron = "0 */1 *? * * THU")
	public void scheduleMessage() {
		log.info("실행시간 webHook: {}", LocalDateTime.now());
		send("test", Message.builder().text("<!here> qna 문서를 작성을 해주세요").build());
	}

	private WebClient initWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config ->
						config.customCodecs().encoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
				).build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.baseUrl(WEB_HOOK_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, TOKEN)
				.build();
	}

	private void send(String url, Object dto) {
		String response = Objects.requireNonNull(webClient.post()
				.body(BodyInserters.fromValue(dto))
				.exchange().block()).bodyToMono(String.class)
				.block();
		log.info("WebClient Response: {}", response);
	}
}
