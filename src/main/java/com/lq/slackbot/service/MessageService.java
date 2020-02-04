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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@Slf4j
public class MessageService {

	private static final String BASE_URL = "https://slack.com/api";
	private static final String POST_MESSAGE = "/chat.postMessage";
	private static final String MODAL_URL = "/views.open";
	private static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");

	private final ObjectMapper objectMapper;
	private final WebClient webClient;

	public MessageService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.webClient = initWebClient();
	}

//	public void sendMessage(EventCallbackRequest request) {
//		log.info("app_mention : {}", request);
//		send("/chat.postMessage", new Message(request.getChannel(), "Hello World!"));
//	}

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

		if (body.getAction().equals("test")) {
			view = "{\n" +
					"\t\"type\": \"modal\",\n" +
					"\t\"title\": {\n" +
					"\t\t\"type\": \"plain_text\",\n" +
					"\t\t\"text\": \"App menu\",\n" +
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
					"\t\t\t\t\"type\": \"mrkdwn\",\n" +
					"\t\t\t\t\"text\": \"*Hi <fakelink.toUser.com|@David>!* Here's how I can help you:\"\n" +
					"\t\t\t}\n" +
					"\t\t},\n" +
					"\t\t{\n" +
					"\t\t\t\"type\": \"divider\"\n" +
					"\t\t},\n" +
					"\t\t{\n" +
					"\t\t\t\"type\": \"section\",\n" +
					"\t\t\t\"text\": {\n" +
					"\t\t\t\t\"type\": \"mrkdwn\",\n" +
					"\t\t\t\t\"text\": \":calendar: *Create event*\\nCreate a new event\"\n" +
					"\t\t\t},\n" +
					"\t\t\t\"accessory\": {\n" +
					"\t\t\t\t\"type\": \"button\",\n" +
					"\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\"text\": \"Create event\",\n" +
					"\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t},\n" +
					"\t\t\t\t\"style\": \"primary\",\n" +
					"\t\t\t\t\"value\": \"click_me_123\"\n" +
					"\t\t\t}\n" +
					"\t\t},\n" +
					"\t\t{\n" +
					"\t\t\t\"type\": \"section\",\n" +
					"\t\t\t\"text\": {\n" +
					"\t\t\t\t\"type\": \"mrkdwn\",\n" +
					"\t\t\t\t\"text\": \":clipboard: *List of events*\\nChoose from different event lists\"\n" +
					"\t\t\t},\n" +
					"\t\t\t\"accessory\": {\n" +
					"\t\t\t\t\"type\": \"static_select\",\n" +
					"\t\t\t\t\"placeholder\": {\n" +
					"\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\"text\": \"Choose list\",\n" +
					"\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t},\n" +
					"\t\t\t\t\"options\": [\n" +
					"\t\t\t\t\t{\n" +
					"\t\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\t\"text\": \"My events\",\n" +
					"\t\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t\t},\n" +
					"\t\t\t\t\t\t\"value\": \"value-0\"\n" +
					"\t\t\t\t\t},\n" +
					"\t\t\t\t\t{\n" +
					"\t\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\t\"text\": \"All events\",\n" +
					"\t\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t\t},\n" +
					"\t\t\t\t\t\t\"value\": \"value-1\"\n" +
					"\t\t\t\t\t},\n" +
					"\t\t\t\t\t{\n" +
					"\t\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\t\"text\": \"Event invites\",\n" +
					"\t\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t\t},\n" +
					"\t\t\t\t\t\t\"value\": \"value-1\"\n" +
					"\t\t\t\t\t}\n" +
					"\t\t\t\t]\n" +
					"\t\t\t}\n" +
					"\t\t},\n" +
					"\t\t{\n" +
					"\t\t\t\"type\": \"section\",\n" +
					"\t\t\t\"text\": {\n" +
					"\t\t\t\t\"type\": \"mrkdwn\",\n" +
					"\t\t\t\t\"text\": \":gear: *Settings*\\nManage your notifications and team settings\"\n" +
					"\t\t\t},\n" +
					"\t\t\t\"accessory\": {\n" +
					"\t\t\t\t\"type\": \"static_select\",\n" +
					"\t\t\t\t\"placeholder\": {\n" +
					"\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\"text\": \"Edit settings\",\n" +
					"\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t},\n" +
					"\t\t\t\t\"options\": [\n" +
					"\t\t\t\t\t{\n" +
					"\t\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\t\"text\": \"Notifications\",\n" +
					"\t\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t\t},\n" +
					"\t\t\t\t\t\t\"value\": \"value-0\"\n" +
					"\t\t\t\t\t},\n" +
					"\t\t\t\t\t{\n" +
					"\t\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\t\"text\": \"Team settings\",\n" +
					"\t\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t\t},\n" +
					"\t\t\t\t\t\t\"value\": \"value-1\"\n" +
					"\t\t\t\t\t}\n" +
					"\t\t\t\t]\n" +
					"\t\t\t}\n" +
					"\t\t},\n" +
					"\t\t{\n" +
					"\t\t\t\"type\": \"actions\",\n" +
					"\t\t\t\"elements\": [\n" +
					"\t\t\t\t{\n" +
					"\t\t\t\t\t\"type\": \"button\",\n" +
					"\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\"text\": \"Send feedback\",\n" +
					"\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t},\n" +
					"\t\t\t\t\t\"value\": \"click_me_123\"\n" +
					"\t\t\t\t},\n" +
					"\t\t\t\t{\n" +
					"\t\t\t\t\t\"type\": \"button\",\n" +
					"\t\t\t\t\t\"text\": {\n" +
					"\t\t\t\t\t\t\"type\": \"plain_text\",\n" +
					"\t\t\t\t\t\t\"text\": \"FAQs\",\n" +
					"\t\t\t\t\t\t\"emoji\": true\n" +
					"\t\t\t\t\t},\n" +
					"\t\t\t\t\t\"value\": \"click_me_123\"\n" +
					"\t\t\t\t}\n" +
					"\t\t\t]\n" +
					"\t\t}\n" +
					"\t]\n" +
					"}";
		}

		ModalResponse response = ModalResponse.builder()
				.trigger_id(body.getTrigger_id())
				.view(view)
				.build();

		log.info("모달리스폰스 : {}", response);
		send(MODAL_URL, response);
	}


	public void sendMessageV2(SlackRequest slackRequest) {
		log.info("slackrequest : {}",slackRequest);
		if (slackRequest.getEvent().getText().contains("123")) {
			send(POST_MESSAGE, Message.builder().channel(slackRequest.getChannel()).text("123").build());
		}
		else if (slackRequest.getEvent().getText().contains("주문")) {
			send(POST_MESSAGE,Message.builder()
					.channel(slackRequest.getChannel())
					.text("test")
					.blocks("[\n" +
							"        {\n" +
							"            \"type\": \"section\",\n" +
							"            \"text\": {\n" +
							"                \"type\": \"mrkdwn\",\n" +
							"                \"text\": \"Danny Torrence left the following review for your property:\"\n" +
							"            }\n" +
							"        },\n" +
							"        {\n" +
							"            \"type\": \"actions\",\n" +
							"            \"elements\": [\n" +
							"                {\n" +
							"                    \"action_id\": \"test\",\n" +
							"                    \"type\": \"button\",\n" +
							"                    \"text\": {\n" +
							"                        \"type\": \"plain_text\",\n" +
							"                        \"text\": \"test\",\n" +
							"                        \"emoji\": false\n" +
							"                    }\n" +
							"                }\n" +
							"            ]\n" +
							"        },\n" +
							"        {\n" +
							"            \"type\": \"actions\",\n" +
							"            \"elements\": [\n" +
							"                {\n" +
							"                    \"action_id\": \"findOrder\",\n" +
							"                    \"type\": \"button\",\n" +
							"                    \"text\": {\n" +
							"                        \"type\": \"plain_text\",\n" +
							"                        \"text\": \"findOrder\",\n" +
							"                        \"emoji\": false\n" +
							"                    }\n" +
							"                }\n" +
							"            ]\n" +
							"        }\n" +
							"    ]")
					.build());
		}
		return;
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