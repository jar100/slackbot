package com.lq.slackbot.worklog.service;

import com.lq.slackbot.worklog.domain.WorkLogRequest;
import com.lq.slackbot.worklog.domain.WorkLogResult;
import com.lq.slackbot.worklog.domain.WorkLogUser;
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

	public WorkLogResult startWork(String slackId) {
		final WorkLogUser login = login(slackId);
		final ClientResponse work = workLogClient.post().uri("/api/work_log").body(BodyInserters.fromValue(login.toWorkLogRequest("WORK"))).exchange().block();
		log.info("work result : {}",work.statusCode());

		if (work.statusCode() != HttpStatus.OK) {
			return WorkLogResult.builder().result(false).userName(login.getReal_name()).build();
		}
		final Mono<String> stringMono = work.bodyToMono(String.class);
		log.info("출근성공 : {}", stringMono.block());
		return WorkLogResult.builder().result(true).userName(login.getReal_name()).build();

	}

	public WorkLogResult endWork(String slackId) {
		final WorkLogUser login = login(slackId);
		final WorkLogRequest request = login.toWorkLogRequest("BYEBYE");
		final ClientResponse byebye = workLogClient.post().uri("/api/work_log").body(BodyInserters.fromValue(request)).exchange().block();
		if (byebye.statusCode() != HttpStatus.OK) {
			return WorkLogResult.builder().result(false).userName(login.getReal_name()).build();
		}
		final Mono<String> stringMono = byebye.bodyToMono(String.class);
		log.info("퇴근성공 : {}", stringMono.block());
//		final String format = String.format("/api/get_all?userId=%s&startDate=%s&endDate=%s", request.getUser_id(), LocalDate.now().toString(), LocalDate.now().toString());
//		log.info("format : {}",format);
//		final ClientResponse dailyWork = workLogClient.get().uri(format).exchange().block();
//		log.info("daily work httpcode; {}",dailyWork.statusCode());
//		final Mono<String> listMono = dailyWork.bodyToMono(String.class);
//		final Mono<List> listMono1 = dailyWork.bodyToMono(List.class);
//		log.info("테스트 : {} , 리스트 : {}",listMono.block(),listMono1.block());
		return WorkLogResult.builder().result(true).userName(login.getReal_name()).build();
	}

	public WorkLogResult vacation(String slackId) {
		final WorkLogUser login = login(slackId);
		final WorkLogRequest request = login.toWorkLogRequest("VACATION");
		final ClientResponse response = workLogClient.post().uri("/api/work_log").body(BodyInserters.fromValue(request)).exchange().block();
		if (response.statusCode() != HttpStatus.OK) {
			return WorkLogResult.builder().result(false).userName(login.getReal_name()).build();
		}
		final Mono<String> stringMono = response.bodyToMono(String.class);
		log.info("휴가성공 : {}", stringMono.block());
		return WorkLogResult.builder().result(true).userName(login.getReal_name()).build();
	}
}
