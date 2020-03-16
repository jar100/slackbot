package com.lq.slackbot.config;

import com.lq.slackbot.utils.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient workLogClient() {
		return WebClient.builder()
				.baseUrl(SystemUtils.WORK_LOG_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
				.build();
	}
}
