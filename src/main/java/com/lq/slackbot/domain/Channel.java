package com.lq.slackbot.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Channel {
	@Id
	private String id;
	private String name;
}
