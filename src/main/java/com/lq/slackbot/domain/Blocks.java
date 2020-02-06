package com.lq.slackbot.domain;

import lombok.Data;

import java.util.List;

@Data
public class Blocks {
	List<ModalBlock> blocks;
}
