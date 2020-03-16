package com.lq.slackbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkLogUser {
	private String id;
	private String email;
	private String profile_url;
	private String real_name;
	private String userUid;


	public WorkLogRequest toWorkLogRequest(String type) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZoneOffset seoulZoneOffset = ZoneOffset.of("+09:00");
		final String time = ZonedDateTime.now(seoulZoneOffset).toString();
		log.info("존데이트타임 time : {}", time);
		return WorkLogRequest.builder()
				.type(type)
				.user_id(this.id)
				.auth_user_id(this.userUid+123)
				.target_date(LocalDate.now().format(formatter))
				.time(time)
				.build();
	}


}
