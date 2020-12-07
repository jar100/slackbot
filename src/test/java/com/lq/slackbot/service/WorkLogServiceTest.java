package com.lq.slackbot.service;

import com.lq.slackbot.worklog.service.WorkLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;


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
		workLogService.startWork("UKTREM9U0");
	}

	@Test
	void byebye() {
		workLogService.endWork("UKTREM9U0");
	}



}