package com.lq.slackbot.service;

import com.lq.slackbot.domain.Channel;
import com.lq.slackbot.domain.ChannelRepository;
import com.lq.slackbot.domain.ChannelResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChannelService {
	private ChannelRepository channelRepository;

	public ChannelService(final ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}

	public List<Channel> Save(List<Channel> channelList) {
		return channelRepository.saveAll(channelList);
	}

	public void deleteAll() {
		channelRepository.deleteAll();
	}
}
