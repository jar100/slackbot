package com.lq.slackbot.sheet.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;
import com.lq.slackbot.domain.BirthdayImg;
import com.lq.slackbot.domain.ScheduleRequest;
import com.lq.slackbot.domain.schedule.Schedule;
import com.lq.slackbot.domain.schedule.ScheduleRepository;
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SheetService {
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private ScheduleRepository scheduleRepository;

	/**
	 * Global instance of the HTTP transport.
	 */
	private static HttpTransport httpTransport;

	static {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Exception e) {
			log.error("GoogleNetHttpTransport.newTrustedTransport() : {}", e.getMessage(), e);
		}
	}

	private Gson gson = new Gson();

	/**
	 * Google Sheet API 권한을 SCOPE로 지정
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	public SheetService(final ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	/**
	 * 인증정보를 통해 권한을 인증한다.
	 */
	public static Credential getServiceAccountAuthorize() throws IOException {
		return GoogleCredential.fromStream(new ByteArrayInputStream(SystemUtils.GOOGLE_SHEET_TOKEN.getBytes())).createScoped(SCOPES);
	}


	/**
	 * Google Credential 정보를 가지고 Google Sheet서비스를 초기화 한다.
	 *
	 * @return 인증이 통과된 Sheets API client service
	 * @throws IOException
	 */
	public static Sheets getSheetsService(AuthMode authMode) throws IOException {
		Credential credential = null;
		if (authMode == AuthMode.SERVICE_ACCOUNT) {
			credential = getServiceAccountAuthorize();
		}
		return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public List<Schedule> getScheduleList() throws IOException {
		return getSheetToSchedule();
	}

	/**
	 * 인증 모드 2개
	 */
	private enum AuthMode {
		SERVICE_ACCOUNT
	}


	public List getSheet() throws IOException {
		// 기호에 따라 OAUTH2.0용 인증이나 서비스 계정으로 인증을 수행 후 Sheet Service 객체를 불러온다.
		Sheets service = getSheetsService(AuthMode.SERVICE_ACCOUNT);

		// 아래의 샘플 구글 시트 URL에서 중간의 문자열이 spreed sheet id에 해당한다.
		// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		//실재문서
//		String spreadsheetId = "18tQPZ7GgWWnqevzdB9gIeB-J1k9kqN7o_WT1c_isWJE";
//		테스트문서
		String spreadsheetId = "102eeX8KzZlmfZAM0XB1LSuZcn0Gv9AONWX_EIB6JSA8";

		String range = "A3:450";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			log.error("No data found.");
		} else {
			for (List row : values) {
				if (row.isEmpty()) {
					log.info(row.get(0).toString() + "  " + row.get(1).toString() + " " + row.get(2).toString());
				}
			}
		}
		return values;
	}

	public List<Schedule> getSheetToSchedule() throws IOException {
		// 기호에 따라 OAUTH2.0용 인증이나 서비스 계정으로 인증을 수행 후 Sheet Service 객체를 불러온다.
		Sheets service = getSheetsService(AuthMode.SERVICE_ACCOUNT);

		// 아래의 샘플 구글 시트 URL에서 중간의 문자열이 spreed sheet id에 해당한다.
		// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		//실재문서
//		String spreadsheetId = "18tQPZ7GgWWnqevzdB9gIeB-J1k9kqN7o_WT1c_isWJE";
//		테스트문서
		String spreadsheetId = "102eeX8KzZlmfZAM0XB1LSuZcn0Gv9AONWX_EIB6JSA8";
		List<Schedule> scheduleList = new ArrayList<>();

		String range = "A3:450";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			log.error("No data found.");
		} else {
			for (List row : values) {
				if (!row.isEmpty()) {
					final Schedule byName = scheduleRepository.findByName(row.get(0).toString());
					if (byName != null) {
						byName.updateData(ScheduleRequest.builder()
								.message("테스트!!!!!\n" +
										"<!here>\n" +
										":birthday-hangul::kiss::car::sunny::han-yo: \n 생일 축하합니다~ 생일 축하합니다~:tada:\n" +
										"사랑하는 :heartpulse::heartbeat:" +
										row.get(0) +
										"님 <@" +row.get(3) + ">"+
										":heartbeat::heartpulse:\n" +
										"생일 축하합니다~~~:clapping:  와아아아아아ㅏㅏㅏ")
								.img(BirthdayImg.ONE.getUrl())
								.cronExpression(toCronExpression(row.get(1).toString()))
								.build());
						scheduleList.add(byName);

					} else {
						final Schedule schedule = Schedule.builder()
								.name(row.get(0).toString())
								.channel("GT9V0K9RS")
								.message(
										"테스트!!!!!\n" +
										"<!here>\n" +
										":birthday-hangul::kiss::car::sunny::han-yo: \n 생일 축하합니다~ 생일 축하합니다~:tada:\n" +
										"사랑하는 :heartpulse::heartbeat:" +
										row.get(0).toString() +
										"님 <@" +row.get(3).toString() + ">"+
										":heartbeat::heartpulse:\n" +
										"생일 축하합니다~~~:clapping:  와아아아아아ㅏㅏㅏ")
								.img(BirthdayImg.ONE.getUrl())
								.used(Boolean.TRUE)
								.cronExpression(toCronExpression(row.get(1).toString()))
								.build();
						scheduleList.add(schedule);
					}
					log.info(row.get(0).toString() + "  " + row.get(1).toString() + " " + row.get(2).toString());
				}
			}
		}
		return scheduleList;
	}

	private String toCronExpression(final String toString) {
		final String[] split = toString.split("/");
		return "0 0 9 " +
				split[1] +
				" " +
				split[0] +
				" ?";
	}

}
