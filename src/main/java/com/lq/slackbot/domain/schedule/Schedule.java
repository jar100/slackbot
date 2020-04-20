package com.lq.slackbot.domain.schedule;

import com.lq.slackbot.domain.ScheduleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

	@Builder.Default
	private String img = null;


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

	public boolean hasImg() {
		if (StringUtils.isEmpty(img)) {
			return false;
		}
		return true;
	}

	public boolean notImg() {
		return !hasImg();
	}

	public void updateData(final ScheduleRequest scheduleRequest) {
		if (scheduleRequest.getImg() != null) {
			this.img = scheduleRequest.getImg();
		}
		if (scheduleRequest.getCronExpression() != null) {
			this.cronExpression = scheduleRequest.getCronExpression();
		}
		if (scheduleRequest.getMessage() != null) {
			this.message = scheduleRequest.getMessage();
		}
	}
}
