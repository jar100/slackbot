package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.schedule.Schedule;
import com.lq.slackbot.domain.schedule.ScheduleRepository;
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
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static WebClient webClient = initWebClient();
	private static ScheduleRepository scheduleRepository;
	private static Gson gson = new Gson();


	public MessageService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	public static void sendMessageByModal(Actions body, String channel) {
		String view = null;
		if (body.getAction().equals("scheduler")) {
			view = createSchedulerBlock();
		}
		if (body.getAction().equals("schedulerList")) {
			view = createSchedulerListBlock();
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
			return;
		}

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

	private static String createSchedulerListBlock() {
		Gson gson = new Gson();
		List<ModalBlock> blockList = getScheduleListBlocks();
		return gson.toJson(blockList);
	}

	private static List<ModalBlock> getScheduleListBlocks() {
		// 스캐줄리스트를 가져온다.
		// 가져온리스트로 블럭을 에드한다.
		final List<Schedule> allByUsed = scheduleRepository.findAllByUsed(true);
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.block_id("schedulerList")
				.text(ModalBlock.Content.builder()
						.type(SystemUtils.PLAIN_TEXT)
						.text(":wave: 반복할 메세지를 세팅해 주세요")
						.emoji(true)
						.build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("divider")
				.build());
		//포문
		if (allByUsed.isEmpty()) {
			return blockList;
		}

		for (Schedule schedule : allByUsed) {
			blockList.add(ModalBlock.builder()
					.block_id("scheduleMessage_" + schedule.getName())
					.type("section")
					.text(ModalBlock.Content.builder().type("mrkdwn").text("*" +
							schedule.getName() +
							"*\n" +
							schedule.getCronExpression() +
							"\n" +
							schedule.getMessage() +
							"\n").build())
					.build());
			blockList.add(ModalBlock.builder()
					.block_id("scheduleUpdate_" + schedule.getName())
					.type("actions")
					.elements(Arrays.asList(
							ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("수정").emoji(true).build()).value("scheduleUpdate_action_" + schedule.getName()).build(),
							ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("삭제").emoji(true).build()).value("scheduleDeleted_action_" + schedule.getName()).build()
					))
					.build());
		}
		return blockList;
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
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("스케줄 생성").emoji(false).build())
								.build(),
						ModalBlock.Elements.builder()
								.action_id("schedulerList")
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("스케줄 리스트").emoji(false).build())
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

	public static void sendByCoffeeRequest(final String channel) {
		send(SystemUtils.POST_MESSAGE, Message.builder()
				.channel(channel)
				.text("커피 뽑기")
				.blocks(coffeeBlack())
				.build());
	}
	private static String coffeeBlack() {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text("참가자는 버튼을 눌러주세요").build())
				.accessory(ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("Choose").build()).value("coffee_into").build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("actions")
				.elements(Arrays.asList(ModalBlock.Elements.builder()
								.action_id("coffe_action")
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("뽑기시작").emoji(false).build())
								.build()
				))
				.build());
		return gson.toJson(blockList);
	}

	private static String updateCoffeeBlack(Actions actions) {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text(String.format(actions.getMessage().getText() + " @%s", actions.getMessage().getUser())).build())
				.accessory(ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("Choose").build()).value("coffee_into").build())
				.build());

		return gson.toJson(blockList);
	}

	public static void update(Actions actions) {
		send(SystemUtils.UPDATE_MESSAGE,Message.builder()
				.channel(actions.getChannel().getId())
				.text("커피 뽑기")
				.ts(actions.getMessage().getTs())
				.blocks(updateCoffeeBlack(actions))
				.build());
	}

	private static String userList(List<SlackUser> users) {
		if (users.isEmpty()) {
			return "";
		}
		String userList = "@" + users.remove(0).getSlackId();
		for (SlackUser user : users) {
			userList = userList + ", @" + user.getSlackId();
		}
		return userList;
	}

	public String getToken() {
		return SystemUtils.TOKEN;
	}

}