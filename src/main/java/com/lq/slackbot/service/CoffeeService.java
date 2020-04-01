package com.lq.slackbot.service;

import com.lq.slackbot.controller.Actions;
import com.lq.slackbot.domain.SlackUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class CoffeeService {

	public String pickUser(List<String> userList) throws NoSuchAlgorithmException {
		Random rand = SecureRandom.getInstanceStrong();
		final int i = rand.nextInt(userList.size());
		return userList.get(i);
	}

	public List<SlackUser> list() {
		return null;
	}

	public ResponseEntity<?> run(Actions actions) throws NoSuchAlgorithmException {
		if(actions.isCoffeeMemberIn()) {
			log.info("coffee text : {}",actions.getMessage().getBlocks().get(1).getText());
			MessageService.update(actions);
			return ResponseEntity.ok().build();
		}
		//커피 뽑기 시작
		//검증
		log.info("스타트 유저 검증 : {}", actions.isStartUser());

		if (actions.isCoffeeDoAction()) {
			final String updateCoffeeMessage = actions.getUpdateCoffeeMessage();
			if (updateCoffeeMessage.isEmpty()) {
				log.info("not send");
				return ResponseEntity.ok().build();
			}
			final List<String> arg = Arrays.asList(updateCoffeeMessage.split(","));
			log.info("유저리스트 : {} isEmpty {} size : {}", arg,arg.isEmpty(), arg.size());

			MessageService.sendByCoffeeResult(actions,pickUser(arg));
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.ok().build();
	}

	public boolean notSend(List<String> users) {
		return users.isEmpty();
	}
}
