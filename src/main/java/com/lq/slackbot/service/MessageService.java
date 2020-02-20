package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.utils.SystemUtils;
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

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public MessageService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}

	public void sendMessageByModal(Actions body) {
		String view = null;
		if (body.getAction().equals("scheduler")) {
			view = createSchedulerBlock();
		}
		System.out.println(view);
		if (StringUtils.isEmpty(view)) {
			return;
		}

		final ModalView modalView = ModalView.builder()
				.type("modal")
				.title(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("b2b 봇").emoji(true).build())
				.submit(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("submit").emoji(true).build())
				.close(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("cancel").emoji(true).build())
				.blocks(view)
				.build();

		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.getTrigger_id())
				.view(modalView)
				.build();

		log.info("모달리스폰스 1 : {}", response);
		send(SystemUtils.MODAL_URL, response);
	}

	private String createSchedulerBlock() {
		Gson gson = new Gson();
		List<ModalBlock> blockList = getScheduleBlocks();
		return gson.toJson(blockList);
	}

	private List<ModalBlock> getScheduleBlocks() {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder()
						.type(SystemUtils.PLAIN_TEXT)
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
						.type(SystemUtils.PLAIN_TEXT)
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
						.type(SystemUtils.PLAIN_TEXT)
						.text("반복시간 세팅")
						.emoji(false)
						.build())
				.element(ModalBlock.Elements.builder().type("plain_text_input").multiline(false).action_id("scheduleTime").build())
				.optional(false)
				.build());
		return blockList;
	}


	public void sendMessageV2(SlackRequest slackRequest) {
		log.info("slackrequest : {}", slackRequest);
		if (slackRequest.getEvent().getText().contains("하이")) {
			send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(slackRequest.getChannel())
					.text("Hello World").build());
		} else if (slackRequest.getEvent().getText().contains("명령어")){
			send(SystemUtils.POST_MESSAGE, Message.builder()
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
						.action_id("scheduler")
						.type("button")
						.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("스케줄링 세팅 (미구현)").emoji(false).build())
						.build()))
				.build());

		return gson.toJson(blockList);
	}


	public void sendMessageV3(String channel, String message) {
		send(SystemUtils.POST_MESSAGE, Message.builder().channel(channel).text(message).build());
	}


	private WebClient initWebClient() {
		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(config ->
						config.customCodecs().encoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
				).build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.baseUrl(SystemUtils.BASE_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, SystemUtils.TOKEN)
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
		return SystemUtils.TOKEN;
	}
}