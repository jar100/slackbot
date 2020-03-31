package com.lq.slackbot.service;

import com.lq.slackbot.domain.*;
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageEventService {
	private MessageService messageService;
	private WorkLogService workLogService;
	private Map<String, List<Restaurant>> slackChannels = new HashMap<>();

	@Autowired
	public MessageEventService(final MessageService messageService, final WorkLogService workLogService) {
		this.messageService = messageService;
		this.workLogService = workLogService;
	}

	public void run(final SlackRequest request) {
		String message = null;
		final String text = request.getEvent().getText();
		log.info("text : {}", text);
		if (!"message_changed".equals(request.getEvent().getSubtype()) ) {
			// 왜 넣었는지 까먹음
			log.info("test message_changed");
		}

		if (StringUtils.isEmpty(text)) {
			log.error("택스트를 찾을 수 없음");
			return;
		}


		// 점심 이벤트
		if (text.contains(MessageEventType.LUNCH.getLabel())) {
			restaurantEvent(request);
			return;
		}


		// todo 출퇴근 서비스, 출퇴근 매서드로 분할 하자. 출퇴근 start
		if (text.contains("출근!")) {
			// 출근컨트롤러
			//todo refactoring 봇 테스트만 메세지 보내게 변경
			String channel = request.getChannel();
			//b2b and test 체널만 개인메세지 가게 수정
			if (channel.equals("GT9V0K9RS") || channel.equals("GHJBH4UG4")) {
				channel = request.getUserId();
				log.info(channel);
			}

			final WorkLogResult result = workLogService.startWork(request.getEvent().getUser());
			if (!result.isResult()) {
				MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
						.channel(channel)
						.text(result.getUserName() + "님 출근 실패!")
						.build());
			}
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(channel)
					.text(String.format("%s 님 출근 완료! %n <https://yanolja-cx-work-log.now.sh/records/%s?startDate=%s&endDate=%s|워크로그에서 확인하기>", result.getUserName(), request.getEvent().getUser(), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
					.build());
			return;
		}
		if (text.contains("퇴근!")) {
			final WorkLogResult result = workLogService.endWork(request.getEvent().getUser());

			//todo refactoring 봇 테스트만 메세지 보내게 변경
			String channel = request.getChannel();
			//b2b and test 체널만 개인메세지 가게 수정
			if (channel.equals("GT9V0K9RS") || channel.equals("GHJBH4UG4")) {
				channel = request.getUserId();
				log.info(channel);
			}


			if (!result.isResult()) {
				MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
						.channel(channel)
						.text(result.getUserName() + "님 퇴근 실패!")
						.build());
			}
			MessageService.send(SystemUtils.POST_MESSAGE, Message.builder()
					.channel(channel)
					.text(String.format("%s 님 퇴근 완료! %n <https://yanolja-cx-work-log.now.sh/records/%s?startDate=%s&endDate=%s|워크로그에서 확인하기>", result.getUserName(), request.getEvent().getUser(), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
					.build());
			return;
		}
		// 출퇴근 end



		if (text.contains("커피!")) {
			MessageService.sendByCoffeeRequest(request.getChannel());
			return;
		}
	}

	private String restaurantEvent(final SlackRequest request) {
		String restaurant = null;
		//체널이 구글닥스랑 메핑되있는지 검증
		//https://docs.google.com/spreadsheets/d/1yHrBv9kbhjSJ0vrGxk_T6_ML9chOICOQrJi9FaY_PfY/edit?usp=sharing
		// 있으면 레스토랑리스트를 구글닥스에서 가져옴


		resetRestaurantByChannel(request);
		restaurant = findRestaurant(request.getChannel());
		messageService.sendMessageV3(request.getChannel(), restaurant);
		return restaurant;
	}

	private void resetRestaurantByChannel(final SlackRequest request) {
		if (request.getEvent().getText().contains("초기화")) {
			slackChannels.put(request.getChannel(), Restaurant.list());
			messageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
	}

	public void resetRestaurant() {
		slackChannels = new HashMap<>();
	}

	public String findRestaurant(String sessionKey) {
		final List<Restaurant> restaurantList = slackChannels.get(sessionKey);
		if (restaurantList == null || restaurantList.isEmpty()) {
			slackChannels.put(sessionKey,Restaurant.list());
		}
		return slackChannels.get(sessionKey).remove(0).getName();
	}
}
