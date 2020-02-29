package com.lq.slackbot.domain.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
	@Id
	private Long id;

	private String name;

	private String cronExpression;

	private String message;

	private boolean use;
}
