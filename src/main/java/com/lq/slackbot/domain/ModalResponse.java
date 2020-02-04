package com.lq.slackbot.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Data
@Builder
public class ModalResponse {
	private String trigger_id;
//	private ModalView view;
	private String view;
}
