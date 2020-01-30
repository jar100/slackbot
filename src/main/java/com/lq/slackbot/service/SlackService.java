package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.EventCallbackRequest;
import com.lq.slackbot.domain.Message;
import com.lq.slackbot.domain.SlackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@Slf4j
public class SlackService {

	private static final String BASE_URL = "https://slack.com/api";
	private static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public SlackService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}

	public void sendMessage(EventCallbackRequest request) {
		send("/chat.postMessage", new Message(request.getChannel(), "Hello World!"));
	}


	public void sendMessageV2(SlackRequest slackRequest) {
		if ("123".equals(slackRequest.getEvent().getText())) {
			send("/chat.postMessage", new Message(slackRequest.getChannel(), "456"));
		}
		return;
	}

	private WebClient initWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config ->
						config.customCodecs().encoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
				).build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.baseUrl(BASE_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, TOKEN)
				.build();
	}

	private void send(String url, Object dto) {
		String response = Objects.requireNonNull(webClient.post()
				.uri(url)
				.body(BodyInserters.fromValue(dto))
				.exchange().block()).bodyToMono(String.class)
				.block();
		log.info("WebClient Response: {}", response);
	}

	public String getToken() {
		return TOKEN;
	}
}