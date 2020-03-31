package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.Message;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
	private final ObjectMapper objectMapper;
	private final WebClient webClient;
	private final MessageEventService messageEventService;

	@Autowired
	public Scheduler(final ObjectMapper objectMapper, final MessageEventService messageEventService) {
		this.objectMapper = objectMapper;
		this.messageEventService = messageEventService;
		this.webClient = initWebClient();
	}


	@Scheduled(cron = "0 */10 *? * * *")
	public void check() {
		log.info("실행시간 nonSleep : {}", LocalDateTime.now());
		final WebClient webClient = WebClient.create(SystemUtils.SLACKBOT_BASE_URL);
		final Mono<String> stringMono = Objects.requireNonNull(webClient.get().uri("/init").exchange().block()).bodyToMono(String.class);
		log.info("nonSleep : {}", stringMono.block());
	}


	@Scheduled(cron = "0 0 15 * * THU")
//	@Scheduled(cron = "0 */1 *? * * THU")
	public void scheduleMessage() {
		log.info("실행시간 webHook: {}", LocalDateTime.now());
		send(Message.builder().text("<!here> \n LQ_TechCS 문서를 작성 해주세요 \n " + SystemUtils.SLACK_BOT_B2B_URL
		).build());
	}

	private WebClient initWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config ->
						config.customCodecs().encoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
				).build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.baseUrl(SystemUtils.WEB_HOOK_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, SystemUtils.TOKEN)
				.build();
	}

	private void send(Object dto) {
		String response = Objects.requireNonNull(webClient.post()
				.body(BodyInserters.fromValue(dto))
				.exchange().block()).bodyToMono(String.class)
				.block();
		log.info("WebClient Response: {}", response);
	}


	@Scheduled(cron = "0 */10 *? * * *")
	private void resetRestaurant() {
		messageEventService.resetRestaurant();
	}

//	@Scheduled(cron = "0 */10 *? * * *")
//	private void getChannelList() {
//
//	}
}
