package com.lq.slackbot.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class SheetServiceTest {
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static String testJson2 = System.getenv("TESTTOKEN");
	private Gson gson = new Gson();

	/**
	 * OAUTH 2.0 연동시 credential을 디스크에 저장할 위치
	 */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

	/**
	 * Global instance of the {@link FileDataStoreFactory}.
	 */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/**
	 * Global instance of the HTTP transport.
	 */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Google Sheet API 권한을 SCOPE로 지정
	 */
	private static final List<String> SCOPES =
			Arrays.asList(SheetsScopes.SPREADSHEETS);

	/**
	 * HTTP_TRANSPORT, DATA_STORE_FACTORY 초기화
	 */
	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * OAUTH 2.0 연동시 사용될 callback용 local receiver 포트 지정
	 */
	private static final int LOCAL_SERVER_RECEIVER_PORT = 8080;

	/**
	 * 인증 모드 2개
	 */
	private enum AuthMode {
		OAUTH20, SERVICE_ACCOUNT
	}


	/**
	 * Service Account용 credentail 생성
	 *
	 * @return Credential object.
	 * @throws IOException
	 */
	public static Credential getServiceAccountAuthorize() throws IOException {
		Credential credential1 = GoogleCredential.fromStream(new ByteArrayInputStream(testJson2.getBytes())).createScoped(SCOPES);
		return credential1;
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
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	@Test
	public void test() throws IOException {
		// 기호에 따라 OAUTH2.0용 인증이나 서비스 계정으로 인증을 수행 후 Sheet Service 객체를 불러온다.
		// Sheets service = getSheetsService(AuthMode.OAUTH20);
		Sheets service = getSheetsService(AuthMode.SERVICE_ACCOUNT);

		// 아래의 샘플 구글 시트 URL에서 중간의 문자열이 spreed sheet id에 해당한다.
		// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
		String spreadsheetId = "18tQPZ7GgWWnqevzdB9gIeB-J1k9kqN7o_WT1c_isWJE";

		String range = "A3:C50";
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("No data found.");
		} else {
			for (List row : values) {
				if (row.size() > 0) {
					System.out.println(row.get(0).toString() + "  " + row.get(1).toString() + " " + row.get(2).toString());
				}
			}
		}
	}
}