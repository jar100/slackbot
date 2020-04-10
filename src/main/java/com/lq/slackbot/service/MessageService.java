package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.restaurant.Restaurant;
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

	public static void sendMessageByModal(Actions body) {
		String view = null;
		if (body.getAction().equals("scheduler")) {
			view = createSchedulerBlock();
		}
		if (body.getAction().equals("schedulerList")) {
			view = ModalBlacktToJson(getScheduleListBlocks(body.getChannelId()));
			sendModal(body, view);
			return;
		}

		if (StringUtils.isEmpty(view)) {
			return;
		}
		sendModal(body, view);
	}

	private static String ModalBlacktToJson(List<ModalBlock> blockList) {
		Gson gson = new Gson();
		return gson.toJson(blockList);
	}


	private static List<ModalBlock> createRestaurantListBlack(final String channelName, final List<Restaurant> byChannelOrderByCountDesc) {
		log.info("bob List {}, channel : {}",byChannelOrderByCountDesc, channelName);
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.block_id("restaurantList")
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
		if (byChannelOrderByCountDesc.isEmpty()) {
			return blockList;
		}

		// 세팅할 리스트들을 넣어준다.
		for (Restaurant object : byChannelOrderByCountDesc) {
			final String text = object.view();
			blockList.add(ModalBlock.builder()
					.block_id(object.blockId())
					.type("section")
					.text(ModalBlock.Content.builder().type("mrkdwn").text(text).build())
					.accessory(ModalBlock.Elements.builder()
							.type("button")
							.action_id(object.onOffAction())
							.value(object.actionValue())
							.text(ModalBlock.Content.builder().type("plain_text").text(object.viewOnOff()).build())
							.style(object.buttonStyle())
							.build())
					.build());
		}
		return blockList;
	}

	private static void sendModal(final Actions body, final String view) {
		final ModalView modalView = getModalView(body, view);
		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.getTrigger_id())
				.view(modalView)
				//.external_id("asewtweqr")
				.build();
		send(SystemUtils.MODAL_URL, response);
	}

	private static ModalView getModalView(final Actions body, final String view) {
		return ModalView.builder()
				.type("modal")
				.callback_id(body.getAction())
				.title(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("b2b 봇").emoji(true).build())
				.submit(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("submit").emoji(true).build())
				.close(ModalView.Content.builder().type(SystemUtils.PLAIN_TEXT).text("cancel").emoji(true).build())
				.blocks(view)
				.build();
	}

	private static List<ModalBlock> getScheduleListBlocks(final String channel) {
		// 스캐줄리스트를 가져온다.
		// 가져온리스트로 블럭을 에드한다.
		final List<Schedule> allByUsed = scheduleRepository.findAllByUsedAndChannel(true,channel);
		log.info("allByUsed {}",allByUsed);
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
					.block_id("scheduleMessage_" + schedule.getIdToString())
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
					//.block_id( schedule.getIdToString())
					.type("actions")
					.elements(Arrays.asList(
							ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("수정").emoji(true).build()).action_id("scheduleUpdate_action").value(schedule.getIdToString()).build(),
							ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("삭제").emoji(true).build()).action_id("scheduleDeleted_action").value(schedule.getIdToString()).build()
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

	public static String createRestaurantBlack(String restaurant) {
		Gson gson = new Gson();
		Random rand = null;
		try {
			rand = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			log.info("rand exception : {}",e.getMessage(), e);
		}

		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text(restaurant).build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("actions")
				.elements(Arrays.asList(
						//모달창
						ModalBlock.Elements.builder()
								.action_id("restaurantList")
								.value("restaurantList_" + rand.nextInt(50))
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("식당리스트").emoji(false).build())
								.build(),
						//메세지수정
						ModalBlock.Elements.builder()
								.action_id("retry_restaurant")
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("다시뽑기").emoji(false).build())
								.build(),
						//메세지수정
						ModalBlock.Elements.builder()
								.action_id("submit_restaurant")
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("선택").emoji(false).build())
								.build()))
				.build());

		return gson.toJson(blockList);
	}


	public static void sendMessageV3(String channel, String message) {
		send(SystemUtils.POST_MESSAGE, Message.builder().channel(channel).text(message).build());
	}

	public static void sendBirthdayMessage(String channel, String message, String img) {
		send(SystemUtils.POST_MESSAGE, ImgMessage.builder().channel(channel).text(message).attachments(Arrays.asList(ImgMessage.Attachment.builder()
//				.image_bytes(229463)
				.image_height(800)
				.image_width(750)
				.image_url(img)
				.build())).build());
	}

	public static void sendMessageByRestaurant(String channel, String restaurant) {
		send(SystemUtils.POST_MESSAGE, Message.builder().channel(channel).text("밥").blocks(createRestaurantBlack(restaurant)).build());
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

	public static void sendByCoffeeRequest(final SlackRequest request) {
		send(SystemUtils.POST_MESSAGE, Message.builder()
				.channel(request.getChannel())
				.text("커피 뽑기")
				.blocks(coffeeBlack(request.getUserId()))
				.build());
	}
	private static String coffeeBlack(final String userId) {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text("참가자는 버튼을 눌러주세요").build())
				.accessory(ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("참가").build()).action_id("coffee_into").value(userId).build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("actions")
				.block_id(userId)
				.elements(Arrays.asList(ModalBlock.Elements.builder()
								.action_id("coffee_action")
								.value(userId)
								.type("button")
								.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("뽑기시작").emoji(false).build())
								.build()
				))
				.build());
		return gson.toJson(blockList);
	}

	private static String updateCoffeeBlack(Actions actions) {
		final String joinUsers = createJoinUser(actions);
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text("참가자는 버튼을 눌러주세요").build())
				.accessory(ModalBlock.Elements.builder().type("button").text(ModalBlock.Content.builder().type("plain_text").text("참가").build()).action_id("coffee_into").value(actions.getValue()).build())
				.build());
		if (!joinUsers.isEmpty()) {
			blockList.add(ModalBlock.builder()
					.type("section")
					.text(ModalBlock.Content.builder().type("mrkdwn").text(joinUsers).build())
					.build());
		}
		blockList.add(ModalBlock.builder()
				.type("actions")
				.block_id(actions.getActions().get(0).getBlock_id())
				.elements(Arrays.asList(ModalBlock.Elements.builder()
						.action_id("coffee_action")
						.value(actions.getValue())
						.type("button")
						.text(ModalBlock.Content.builder().type(SystemUtils.PLAIN_TEXT).text("뽑기시작").emoji(false).build())
						.build()
				))
				.build());
		return gson.toJson(blockList);
	}

	private static String createJoinUser(final Actions actions) {
		String comma = "";
		if (actions.getUpdateCoffeeMessage().length() > 1) {
			comma = ",";
		}
		final List<String> beforeUsers = new ArrayList<>(Arrays.asList(actions.getUpdateCoffeeMessage().split(",")));
		final String target = String.format("<@%s>", actions.getUser().getId());
		log.info("택스트 변환전 : {}", beforeUsers);
		if(beforeUsers.contains(target)) {
			beforeUsers.remove(target);
			String message = "";
			log.info("택스트 변환 후 : {}", beforeUsers);
			if (beforeUsers.size() == 0) {
				log.info("택스트 엠티리스트 출력 ");
				return message;
			}
			message = beforeUsers.remove(0);
			for (String beforeUser : beforeUsers) {
				message = message + comma + beforeUser;
			}
			return message;
		}
		return String.format(actions.getUpdateCoffeeMessage() + comma + "<@%s>", actions.getUser().getId());
	}

	public static void sendByCoffeeResult(Actions actions, String user) {
		send(SystemUtils.UPDATE_MESSAGE, Message.builder()
				.channel(actions.getChannel().getId())
				.text("커피 뽑기")
				.ts(actions.getMessage().getTs())
				.blocks(updateCoffeeBlackOk(actions,user))
				.build());
	}
	public static String updateCoffeeBlackOk(Actions actions, String user) {
		List<ModalBlock> blockList = new ArrayList();
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text("뽑기가 완료 되었습니다.").build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text(actions.getUpdateCoffeeMessage()).build())
				.build());
		blockList.add(ModalBlock.builder()
				.type("section")
				.text(ModalBlock.Content.builder().type("mrkdwn").text(String.format("당첨자는 :tada: %s :tada: 입니다.", user)).build())
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

	public static void sendMessageByModalV2(final Actions actions, final List<Restaurant> byChannelOrderByCountDesc) {
		String view = null;
		if(actions.isRestaurantList()) {
			view = ModalBlacktToJson(createRestaurantListBlack(actions.getChannelId(),byChannelOrderByCountDesc));
			sendModal(actions, view);
			return;
		}
	}

	public static void sendMessageByModalUpdate(final Actions actions, final List<Restaurant> byChannelOrderByCountDesc) {
		String view = null;
		if(actions.isRestaurantOnOff()) {
			view = ModalBlacktToJson(createRestaurantListBlack(actions.getChannelId(),byChannelOrderByCountDesc));
			sendModalUpdate(actions, view);
			return;
		}
	}

	private static void sendModalUpdate(final Actions actions, final String view) {
		final ModalView modalView = getModalView(actions, view);
		ModalResponse response = ModalResponse.builder()
				.trigger_id(actions.getTrigger_id())
				.view(modalView)
				.view_id(actions.getRootViewId())
				.build();
		log.info("업데이트 : {}",gson.toJson(response));
		send(SystemUtils.MODAL_UPDATE_URL, response);
	}

	public static void updateByRestaurant(Actions actions, String restaurant) {
		send(SystemUtils.UPDATE_MESSAGE,Message.builder()
				.channel(actions.getChannel().getId())
				.text("밥")
				.ts(actions.getMessage().getTs())
				.blocks(createRestaurantBlack(restaurant))
				.build());
	}

	public static void updateResult(Actions actions, String message) {
		send(SystemUtils.UPDATE_MESSAGE,Message.builder()
				.channel(actions.getChannel().getId())
				.text(message)
				.ts(actions.getMessage().getTs())
				.build());
	}

	public String getToken() {
		return SystemUtils.TOKEN;
	}

}