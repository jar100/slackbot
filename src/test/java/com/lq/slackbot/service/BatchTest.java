package com.lq.slackbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gh.baek
 */
class BatchTest {

	private static String API = "http://lqt-b2b-api.stage.yanolja.in/";
	private static String BATCH_URL = "/admin/api/phoneNumber/modify/start";
	private static String STOP_URL = "/admin/api/phoneNumber/modify/stop";
	private static String RUN_CHECK_URL = "/admin/api/phoneNumber/modify/stat";


	@Test
	void Check() {
		//Given
		RestTemplate restTemplate = new RestTemplate();
//		24258190
		int initialSeq = 14215012;
		//When
		final Stat forObject = restTemplate.getForObject(API + RUN_CHECK_URL + "?initialSeq=" + initialSeq, Stat.class);
		System.out.println(forObject);
		//Then
	}
//8494484  12608780
//	@Test
//	void stop() {
//		//Given
//		RestTemplate restTemplate = new RestTemplate();
//		int initialSeq = 14215012;
//		//When
//		restTemplate.postForEntity(API + STOP_URL + "?initialSeq=" + initialSeq, null, String.class);
//		//Then
//
//	}

	@Test
	void start() {
		//Given
		RestTemplate restTemplate = new RestTemplate();
		//1   14215012   10068190
		//2   10068190   8068190
		//3   8068190  7636844 6068190
		//4 7636844 6068190
		//5 7546289  5546289
		//6 10780300 8068190
		//7 5_546_289 3546289
		// 8 3546290 0
		//1323097 0
		// 675745 0
		int initialSeq = 329287;
		int fetchCount = 329287;
		int finalSeq = 0;

		//When
		final ResponseEntity<String> start = restTemplate.postForEntity(API + BATCH_URL + "?initialSeq=" + initialSeq + "&finalSeq=" + finalSeq + "&fetchCount=" + fetchCount, null, String.class);
		System.out.println(start.getBody());
		//Then
	}



	public static class Stat {
		private LocalDate lastProcessedDate;
		private LocalDateTime lastProcessedDateTime;
		private long lastProcessedSeq;
		private boolean finished;
		private String exception;


		@Override
		public String toString() {
			return "Stat{" +
					"lastProcessedDate=" + lastProcessedDate +
					", lastProcessedDateTime=" + lastProcessedDateTime +
					", lastProcessedSeq=" + lastProcessedSeq +
					", finished=" + finished +
					", exception='" + exception + '\'' +
					'}';
		}

		public LocalDate getLastProcessedDate() {
			return lastProcessedDate;
		}

		public void setLastProcessedDate(final LocalDate lastProcessedDate) {
			this.lastProcessedDate = lastProcessedDate;
		}

		public LocalDateTime getLastProcessedDateTime() {
			return lastProcessedDateTime;
		}

		public void setLastProcessedDateTime(final LocalDateTime lastProcessedDateTime) {
			this.lastProcessedDateTime = lastProcessedDateTime;
		}

		public long getLastProcessedSeq() {
			return lastProcessedSeq;
		}

		public void setLastProcessedSeq(final long lastProcessedSeq) {
			this.lastProcessedSeq = lastProcessedSeq;
		}

		public boolean isFinished() {
			return finished;
		}

		public void setFinished(final boolean finished) {
			this.finished = finished;
		}

		public String getException() {
			return exception;
		}

		public void setException(final String exception) {
			this.exception = exception;
		}
	}
}
