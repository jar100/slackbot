package com.lq.slackbot.worklog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
		return WorkLogRequest.builder()
				.type(type)
				.user_id(this.id)
				.auth_user_id(this.userUid)
				.target_date(LocalDate.now().format(formatter))
				.time(ZonedDateTime.now(seoulZoneOffset).toString())
				.build();
	}
}
