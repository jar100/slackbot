package com.lq.slackbot.service;

import com.google.gson.Gson;
import com.lq.slackbot.domain.MessageEventType;
import com.lq.slackbot.domain.ModalResponseV2;
import com.lq.slackbot.domain.ModalView;
import com.lq.slackbot.domain.Restaurant;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MessageEventServiceTest {
	@Autowired
	private MessageEventService messageEventService;

	@Test
	void name() {
		final double random = Math.random() * (Restaurant.values().length - 1);
		System.out.println(random);

	}

	@Test
	void name3() {
		final boolean contains = "점심!".contains(MessageEventType.LUNCH.getLabel());
		System.out.println(contains);
	}

	@Test
	void modalViewV1VsV2() {
		String v1 = "\"\n" +
				"{\n" +
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
				"})\"";


		final ModalView build = ModalView.builder()
				.type("modal")
				.title(ModalView.Content.builder().type("plain_text").text("주문검색").emoji(true).build())
				.submit(ModalView.Content.builder().type("palin_text").text("Submit").emoji(true).build())
				.close(ModalView.Content.builder().type("plain_text").text("Cancel").emoji(true).build())
				.blocks("\"\n" +
						"{\n" +
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
						"})\"")
				.build();

		Gson gson = new Gson();

		final ModalResponseV2 build1 = ModalResponseV2.builder().trigger_id("123").view(gson.toJson(build)).build();
		final ModalResponseV2 build2 = ModalResponseV2.builder().trigger_id("123").view(v1).build();

		final Mono<ClientResponse> exchange = WebClient.create("https://c3b0a121.ngrok.io/").post().uri("test").body(BodyInserters.fromValue(build1)).exchange();


		///
		final Mono<ClientResponse> exchange2 = WebClient.create("https://c3b0a121.ngrok.io/").post().uri("test").body(BodyInserters.fromValue(build2)).exchange();
		System.out.println(exchange.block().bodyToMono(String.class));

		System.out.println(exchange2.block().bodyToMono(String.class));

	}


//	@Test
//	void stringParse() {
//		//given
//		String test = "밥! (123,ㅂㅈㄷ,5454,ㅊㅇㄹㄷㄱ,ㅁㅇㄴㅁㄹㄷㄱ,)";
//
//		//when
//		String[] text = messageEventService.parseMessage(test);
//
//		System.out.println(text);
//
//		//then
//		assertThat(text).isEqualTo("123,ㅂㅈㄷ,5454,ㅊㅇㄹㄷㄱ,ㅁㅇㄴㅁㄹㄷㄱ,");
//	}
}