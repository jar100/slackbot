package com.lq.slackbot.domain.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String cronExpression;

	private String message;

	private String channel;

	private String target;

	private Boolean used;


	public JobRequest toJobRequest(){
		return JobRequest.builder().jobGroup(this.channel).jobName(this.name).cronExpression(this.cronExpression).jobDataMap(this.message).build();
	}

	public Schedule unUsed() {
		this.used = false;
		return this;
	}

	public String getIdToString() {
		return "" + id;
	}

	public boolean isUpdateJob() {
		return id != null;
	}
}
