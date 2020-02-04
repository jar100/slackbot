package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lq.slackbot.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MessageService {

	private static final String BASE_URL = "https://slack.com/api";
	private static final String POST_MESSAGE = "/chat.postMessage";
	private static final String MODAL_URL =  "/views.open";
	private static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public MessageService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}

	public void sendMessage(EventCallbackRequest request) {
		log.info("app_mention : {}", request);
		send("/chat.postMessage", new Message(request.getChannel(), "Hello World!"));
	}

	public void sendMessageByModal(Map<String,String> body) {
		for (String s : body.keySet()) {
			log.info("body key {}, value {}",s,body.get(s));
		}
		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.get("trigger_id"))
				.view("{\n" +
						"\t\"type\": \"modal\",\n" +
						"\t\"title\": {\n" +
						"\t\t\"type\": \"plain_text\",\n" +
						"\t\t\"text\": \"주문 검색\",\n" +
						"\t\t\"emoji\": true\n" +
						"\t},\n" +
						"\t\"submit\": {\n" +
						"\t\t\"type\": \"plain_text\",\n" +
						"\t\t\"text\": \"Submit\",\n" +
						"\t\t\"emoji\": true\n" +
						"\t},\n" +
						"\t\"close\": {\n" +
						"\t\t\"type\": \"plain_text\",\n" +
						"\t\t\"text\": \"Cancel\",\n" +
						"\t\t\"emoji\": true\n" +
						"\t},\n" +
						"\t\"blocks\": [\n" +
						"\t\t{\n" +
						"\t\t\t\"type\": \"section\",\n" +
						"\t\t\t\"text\": {\n" +
						"\t\t\t\t\"type\": \"plain_text\",\n" +
						"\t\t\t\t\"text\": \":wave: 찾을 주문을 검색해 주세요\",\n" +
						"\t\t\t\t\"emoji\": true\n" +
						"\t\t\t}\n" +
						"\t\t},\n" +
						"\t\t{\n" +
						"\t\t\t\"type\": \"divider\"\n" +
						"\t\t},\n" +
						"        {\n" +
						"\t\t\t\"type\": \"input\",\n" +
						"\t\t\t\"label\": {\n" +
						"\t\t\t\t\"type\": \"plain_text\",\n" +
						"\t\t\t\t\"text\": \"이름?\",\n" +
						"\t\t\t\t\"emoji\": true\n" +
						"\t\t\t},\n" +
						"\t\t\t\"element\": {\n" +
						"\t\t\t\t\"type\": \"plain_text_input\",\n" +
						"\t\t\t\t\"multiline\": false,\n" +
						"                \"action_id\": \"name\"\n" +
						"\t\t\t},\n" +
						"\t\t\t\"optional\": true\n" +
						"\t\t},\n" +
						"        {\n" +
						"\t\t\t\"type\": \"input\",\n" +
						"\t\t\t\"label\": {\n" +
						"\t\t\t\t\"type\": \"plain_text\",\n" +
						"\t\t\t\t\"text\": \"전화번호\",\n" +
						"\t\t\t\t\"emoji\": false\n" +
						"\t\t\t},\n" +
						"\t\t\t\"element\": {\n" +
						"\t\t\t\t\"type\": \"plain_text_input\",\n" +
						"\t\t\t\t\"multiline\": false,\n" +
						"                \"action_id\": \"call\"\n" +
						"\t\t\t},\n" +
						"\t\t\t\"optional\": true\n" +
						"\t\t},\n" +
						"        \n" +
						"\t\t{\t\n" +
						"\t\t\t\"type\": \"input\",\n" +
						"\t\t\t\"label\": {\n" +
						"\t\t\t\t\"type\": \"plain_text\",\n" +
						"\t\t\t\t\"text\": \"쿠폰번호?\",\n" +
						"\t\t\t\t\"emoji\": false\n" +
						"\t\t\t},\n" +
						"\t\t\t\"element\": {\n" +
						"\t\t\t\t\"type\": \"plain_text_input\",\n" +
						"\t\t\t\t\"multiline\": false,\n" +
						"                \"action_id\": \"couponCd\"\n" +
						"\t\t\t},\n" +
						"\t\t\t\"optional\": true\n" +
						"\t\t},\n" +
						"        {\n" +
						"\t\t\t\"type\": \"input\",\n" +
						"\t\t\t\"label\": {\n" +
						"\t\t\t\t\"type\": \"plain_text\",\n" +
						"\t\t\t\t\"text\": \"주문번호,핀?\",\n" +
						"\t\t\t\t\"emoji\": false\n" +
						"\t\t\t},\n" +
						"\t\t\t\"element\": {\n" +
						"\t\t\t\t\"type\": \"plain_text_input\",\n" +
						"\t\t\t\t\"multiline\": false,\n" +
						"                \"action_id\": \"pin\"\n" +
						"\t\t\t},\n" +
						"\t\t\t\"optional\": true\n" +
						"\t\t}\n" +
						"\t]\n" +
						"}")
				.build();

		log.info("모달리스폰스 : {}",response);
		send(MODAL_URL, response);
	}


	public void sendMessageV2(SlackRequest slackRequest) {
		if ("123".equals(slackRequest.getEvent().getText())) {
			send("/chat.postMessage", new Message(slackRequest.getChannel(), "456"));
		}
		return;
	}

	public void sendMessageV3(String channel, String message) {
		send(POST_MESSAGE, new Message(channel, message));
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