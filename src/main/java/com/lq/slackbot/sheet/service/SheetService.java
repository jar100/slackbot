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
import com.lq.slackbot.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SheetService {
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

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
		String spreadsheetId = "18tQPZ7GgWWnqevzdB9gIeB-J1k9kqN7o_WT1c_isWJE";

		String range = "A3:C50";
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

}
