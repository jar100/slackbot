package com.lq.slackbot.service;

import com.lq.slackbot.domain.*;
import com.lq.slackbot.domain.restaurant.RestaurantEnum;
import com.lq.slackbot.worklog.domain.WorkLogResult;
import com.lq.slackbot.utils.SystemUtils;
import com.lq.slackbot.worklog.service.WorkLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
	private RestaurantService restaurantService;
	private Map<String, List<RestaurantEnum>> slackChannels = new HashMap<>();

	@Autowired
	public MessageEventService(final MessageService messageService, final WorkLogService workLogService, final RestaurantService restaurantService) {
		this.messageService = messageService;
		this.workLogService = workLogService;
		this.restaurantService = restaurantService;
	}

	/**
	 * 응답을 먼저 반환해야 slack에서 재요청을 안보냄 그래서 비동기처리
	 * 여기도 리퀘스트 통일시켜야함... slackRequest == Actions
	 */
//	@Async("threadPoolTaskExecutor")
	public void run(final SlackRequest request) {
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
//		if (text.contains(MessageEventType.LUNCH.getLabel())) {
//			restaurantEvent(request);
//			return;
//		}

		if (text.contains("밥!")) {
			restaurantService.restaurantEvent(request);
			return;
		}

		// todo 출퇴근 서비스, 출퇴근 매서드로 분할 하자. 출퇴근 start
		// 분기처리와 엑션처리를 분리해야한다. 분기 엑션 한번에 하면 별로임.
		if (work(request, text)) return;
		// 출퇴근 end

		if (text.contains("커피!")) {
			MessageService.sendByCoffeeRequest(request);
			return;
		}
	}


	//todo  출퇴근 컨트롤러로 빼자
	private boolean work(final SlackRequest request, final String text) {
		if (text.contains("출근!")) {
			// 출근컨트롤러
			//todo refactoring 봇 테스트만 메세지 보내게 변경
			String channel = request.getChannel();
//			final WorkLogResult result = workLogService.startWork(request.getEvent().getUser(), text);
            final WorkLogResult result = WorkLogResult.builder().result(true).build();
			if (!result.isResult()) {
				MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
						.attachments("출퇴근")
						.user(request.getUserId())
						.channel(channel)
						.text(result.getUserName() + "님 출근 실패!")
						.build());
				return true;
			}
			MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
					.attachments("출퇴근")
					.user(request.getUserId())
					.channel(channel)
					.text(String.format("<@%s> 님 출근 완료!" , request.getUserId()))
					.build());
			return true;
		}
		if (text.contains("퇴근!")) {
//			final WorkLogResult result = workLogService.endWork(request.getEvent().getUser());
			String channel = request.getChannel();
            final WorkLogResult result = WorkLogResult.builder().result(true).build();
            //b2b and test 체널만 개인메세지 가게 수정
			if (!result.isResult()) {
				MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
						.attachments("출퇴근")
						.user(request.getUserId())
						.channel(channel)
						.text(result.getUserName() + "님 퇴근 실패!")
						.build());
				return true;
			}
			MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
					.attachments("출퇴근")
					.user(request.getUserId())
					.channel(channel)
					.text(String.format("%s 님 퇴근 완료! %n <https://docs.google.com/forms/d/e/1FAIpQLScKOLHZmAl6REIRJyKirSevGYhw-e8237Ram3Y8OfTIMKjV3g/viewform|원격근무 업무일지 작성>", request.getUserId()))
					.build());
			return true;
		}
//		if (text.contains("휴가!")) {
//			final WorkLogResult result = workLogService.vacation(request.getEvent().getUser());
//			String channel = request.getChannel();
//			//b2b and test 체널만 개인메세지 가게 수정
//			if (!result.isResult()) {
//				MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
//						.attachments("출퇴근")
//						.user(request.getUserId())
//						.channel(channel)
//						.text(result.getUserName() + "님 휴가 실패!")
//						.build());
//			}
//			MessageService.send(SystemUtils.POST_EPHEMERAL, Message.builder()
//					.attachments("출퇴근")
//					.user(request.getUserId())
//					.channel(channel)
//					.text(String.format("%s 님 휴가 사용 성공! %n <https://yanolja-cx-work-log.now.sh/records/%s?startDate=%s&endDate=%s|워크로그에서 확인하기>", result.getUserName(), request.getEvent().getUser(), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
//					.build());
//			return true;
//		}
		return false;
	}

	public void resetRestaurant() {
		slackChannels = new HashMap<>();
	}

	private String restaurantEvent(final SlackRequest request) {
		String restaurant = null;
		resetRestaurantByChannel(request);
		restaurant = findRestaurant(request.getChannel());
		messageService.sendMessageV3(request.getChannel(), restaurant);
		return restaurant;
	}

	private void resetRestaurantByChannel(final SlackRequest request) {
		if (request.getEvent().getText().contains("초기화")) {
			slackChannels.put(request.getChannel(), RestaurantEnum.list());
			messageService.sendMessageV3(request.getChannel(),"초기화 합니다.");
		}
	}

	public String findRestaurant(String sessionKey) {
		final List<RestaurantEnum> restaurantList = slackChannels.get(sessionKey);
		if (restaurantList == null || restaurantList.isEmpty()) {
			slackChannels.put(sessionKey, RestaurantEnum.list());
		}
		return slackChannels.get(sessionKey).remove(0).getName();
	}
}
