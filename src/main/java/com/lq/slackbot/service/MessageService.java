package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Slf4j
public class MessageService {

	private static final String BASE_URL = "https://slack.com/api";
	private static final String POST_MESSAGE = "/chat.postMessage";
	private static final String MODAL_URL = "/views.open";
	private static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");
	private static final String PLAIN_TEXT = "plain_text";


	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public MessageService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}

	public void sendMessageByModal(Actions body) {
		String view = null;
		if (body.getAction().equals("findOrder")) {
			view = "{\n" +
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
					"}";
		}

		if (body.getAction().equals("scheduler")) {
			view = createSchedulerBlock();
		}
		System.out.println(view);
		if (StringUtils.isEmpty(view)) {
			return;
		}

		final ModalView modalView = ModalView.builder()
				.type("modal")
				.title(ModalView.Content.builder().type(PLAIN_TEXT).text("b2b 봇").emoji(true).build())
				.submit(ModalView.Content.builder().type(PLAIN_TEXT).text("submit").emoji(true).build())
				.close(ModalView.Content.builder().type(PLAIN_TEXT).text("cancel").emoji(true).build())
				.blocks(view)
				.build();

		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.getTrigger_id())
				.view(modalView)
				.build();

		log.info("모달리스폰스 1 : {}", response);
		send(MODAL_URL, response);
	}

	private String createSchedulerBlock() {
		Gson gson = new Gson();
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder()
						.type(PLAIN_TEXT)
						.text(":wave: 반복할 메세지를 세팅해 주세요 (1건만 됨)")
						.emoji(true)
						.build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("divider")
				.build());
		blockList.add(ModalBlock.builder()
				.block_id("scheduleMessage")
				.type("input")
				.label(ModalBlock.Content.builder()
						.type(PLAIN_TEXT)
						.text("메세지 세팅")
						.emoji(false)
						.build())
				.element(ModalBlock.Elements.builder().type("plain_text_input").multiline(false).action_id("scheduleMessage").build())
				.optional(false)
				.build());
		blockList.add(ModalBlock.builder()
				.block_id("scheduleTime")
				.type("input")
				.label(ModalBlock.Content.builder()
						.type(PLAIN_TEXT)
						.text("반복시간 세팅")
						.emoji(false)
						.build())
				.element(ModalBlock.Elements.builder().type("plain_text_input").multiline(false).action_id("scheduleTime").build())
				.optional(false)
				.build());
		return gson.toJson(blockList);
	}


	public void sendMessageV2(SlackRequest slackRequest) {
		log.info("slackrequest : {}", slackRequest);
		if (slackRequest.getEvent().getText().contains("하이")) {
			send(POST_MESSAGE, Message.builder().channel(slackRequest.getChannel()).text("Hello World").build());
		} else {
			send(POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("test")
					.blocks(createSelectBlock())
					.build());
		}
	}

	private String createSelectBlock() {
		Gson gson = new Gson();
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text("기능").build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("actions")
				.elements(Arrays.asList(ModalBlock.Elements.builder()
						.action_id("findOrder")
						.type("button")
						.text(ModalBlock.Content.builder().type(PLAIN_TEXT).text("주문 찾기 (미구현)").emoji(false).build())
						.build()))
				.build());
		blockList.add(ModalBlock.builder()
				.type("actions")
				.elements(Arrays.asList(ModalBlock.Elements.builder()
						.action_id("scheduler")
						.type("button")
						.text(ModalBlock.Content.builder().type(PLAIN_TEXT).text("스케줄링 세팅 (미구현)").emoji(false).build())
						.build()))
				.build());

		return gson.toJson(blockList);
	}


	public void sendMessageV3(String channel, String message) {
		send(POST_MESSAGE, Message.builder().channel(channel).text(message).build());
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