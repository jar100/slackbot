package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static WebClient webClient = initWebClient();

	public static void sendMessageByModal(Actions body, String channel) {
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
				.callback_id("scheduleModal_" + channel)
				.title(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("b2b 봇").emoji(true).build())
				.submit(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("submit").emoji(true).build())
				.close(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("cancel").emoji(true).build())
				.blocks(view)
				.build();

		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.getTrigger_id())
				.view(modalView)
				.build();

		send(SystemUtils.MODAL_URL, response);
	}

	private static String createSchedulerBlock() {
		Gson gson = new Gson();
		List<ModalBlock> blockList = getScheduleBlocks();
		return gson.toJson(blockList);
	}

	private static List<ModalBlock> getScheduleBlocks() {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.block_id("schedulerSubmit")
				.text(ModalBlock.Content.builder()
						.type(SystemUtils.PLAIN_TEXT)
						.text(":wave: 반복할 메세지를 세팅해 주세요")
						.emoji(true)
						.build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("divider")
				.build());
		blockList.add(ModalBlock.builder()
				.block_id("scheduleTitle")
				.type("input")
				.label(ModalBlock.Content.builder()
						.type(SystemUtils.PLAIN_TEXT)
						.text("제목 세팅")
						.emoji(false)
						.build())
				.element(ModalBlock.Elements.builder().type("plain_text_input").multiline(false).action_id("scheduleTitle").build())
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
		blockList.add(ModalBlock.builder()
				.block_id("scheduleMessage")
				.type("input")
				.label(ModalBlock.Content.builder()
						.type(SystemUtils.PLAIN_TEXT)
						.text("메세지 세팅")
						.emoji(false)
						.build())
				.element(ModalBlock.Elements.builder().type("plain_text_input").multiline(true).action_id("scheduleMessage").build())
				.optional(false)
				.build());
		return blockList;
	}

	public static String createSelectBlock() {
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
						.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("스케줄링 세팅").emoji(false).build())
						.build()))
				.build());

		return gson.toJson(blockList);
	}


	public static void sendMessageV3(String channel, String message) {
		send(SystemUtils.POST_MESSAGE, Message.builder().channel(channel).text(message).build());
	}


	private static WebClient initWebClient() {
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

	public static void send(String url, Object dto) {
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