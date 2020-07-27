package com.lq.slackbot.config;

import com.lq.slackbot.utils.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient workLogClient() {
		return WebClient.builder()
				.baseUrl(SystemUtils.WORK_LOG_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
				.build();
	}

	@Bean
	public WebClient googleClient() {
		return WebClient.builder()
				.baseUrl(SystemUtils.GOOGLE_SPREADSHEETS_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
				.build();
	}

	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(15);
		taskExecutor.setMaxPoolSize(80);
		taskExecutor.setQueueCapacity(30);
		taskExecutor.setThreadNamePrefix("Executor-");
		taskExecutor.initialize();
		return taskExecutor;
	}

}
