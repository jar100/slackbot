package com.lq.slackbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.BirthdayImg;
import com.lq.slackbot.domain.Message;
import com.lq.slackbot.service.MessageEventService;
import com.lq.slackbot.service.MessageService;
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



//	끝 성공
	public void birthday() {
		log.info("실행시간 webHook: {}", LocalDateTime.now());
		//on_lqt
		String channel = "GHCQ4856Y";
		//계정
		String name = "UMTHUUDD2";
		MessageService.sendBirthdayMessage(channel, "<!here>\n" +
				":birthday-hangul::kiss::car::sunny::han-yo: \n 생일 축하합니다~ 생일 축하합니다~:tada:\n" +
				"사랑하는 :heartpulse::heartbeat:" +
				"<@" +name + ">"+
				":heartbeat::heartpulse:\n" +
				"생일 축하합니다~~~:clapping:  와아아아아아ㅏㅏㅏ", BirthdayImg.PHOTO_3.getUrl());
	}


//	@Scheduled(cron = "0 0 1 * * ?")
	public void birthdayList() {

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
