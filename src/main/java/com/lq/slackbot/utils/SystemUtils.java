package com.lq.slackbot.utils;

public class SystemUtils {
	public static final String SLACKBOT_BASE_URL = System.getenv("SLACKBOT_BASE_URL");
	public static final String TOKEN = "Bearer " + System.getenv("BOT_TOKEN");
	public static final String WEB_HOOK_URL = System.getenv("WEB_HOOK_URL");
	public static final String BASE_URL = "https://slack.com/api";
	public static final String POST_MESSAGE = "/chat.postMessage";
	public static final String MODAL_URL = "/views.open";
	public static final String PLAIN_TEXT = "plain_text";
	public static final String SLACK_BOT_B2B_URL = System.getenv("SLACK_BOT_B2B_URL");
}
