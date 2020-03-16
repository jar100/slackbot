package com.lq.slackbot.service;

import com.lq.slackbot.domain.WorkLogUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WorkLogService {
	WebClient workLogClient;

	public WorkLogService(final WebClient workLogClient) {
		this.workLogClient = workLogClient;
	}

	public WorkLogUser login(String slackId) {
		final Mono<WorkLogUser> workLogUserMono = workLogClient.get().uri("/api/get_user?userId=" + slackId).retrieve().bodyToMono(WorkLogUser.class);
		log.info("응답데이터 : {}", workLogUserMono.block());
		return workLogUserMono.block();
	}

	public String startJob(String slackId) {
		final WorkLogUser login = login(slackId);
		final ClientResponse work = workLogClient.post().uri("/api/work_log").body(BodyInserters.fromValue(login.toWorkLogRequest("WORK"))).exchange().block();
		assert work != null;
		if (work.statusCode() != HttpStatus.OK) {
			return "요청 실패";
		}
		final Mono<String> stringMono = work.bodyToMono(String.class);
		log.info("출근성공 : {}", stringMono.block());
		return "요청 성공";
	}

	public String endJob(String slackId) {
		final WorkLogUser login = login(slackId);
		final ClientResponse byebye = workLogClient.post().uri("/api/work_log").body(BodyInserters.fromValue(login.toWorkLogRequest("BYEBYE"))).exchange().block();
		assert byebye != null;
		if (byebye.statusCode() != HttpStatus.OK) {
			return "요청 실패";
		}
		final Mono<String> stringMono = byebye.bodyToMono(String.class);
		log.info("퇴근성공 : {}", stringMono.block());
		return "요청 성공";
	}
}
