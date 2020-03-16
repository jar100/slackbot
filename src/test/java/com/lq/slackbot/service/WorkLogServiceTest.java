package com.lq.slackbot.service;

import com.lq.slackbot.utils.SystemUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("dev")
class WorkLogServiceTest {
//	WebClient webClient;

	@Autowired
	WorkLogService workLogService;

//	@BeforeAll
//	public void init() {
//		webClient = WebClient.builder()
//				.baseUrl(SystemUtils.WORK_LOG_URL)
//				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.build();
//	}

	@Test
	void loginTest() {
		workLogService.login("UKTREM9U0");
	}

	@Test
	void name() {
		workLogService.startJob("UKTREM9U0");
	}

	@Test
	void byebye() {
		workLogService.endJob("UKTREM9U0");
	}
}