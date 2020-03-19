package com.lq.slackbot.service;

import com.lq.slackbot.domain.SlackUser;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class CoffeeService {

	public String pickUser(List<String> userList) throws NoSuchAlgorithmException {
		Random rand = SecureRandom.getInstanceStrong();
		final int i = rand.nextInt(userList.size());
		return userList.get(i);
	}

	public List<SlackUser> list() {
		return null;
	}
}
