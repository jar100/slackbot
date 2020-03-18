package com.lq.slackbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

class WorkLogServiceTest2 {
	@Test
	void name() {
		final String format = LocalDate.now().toString();
		System.out.println(format);
		Gson gson = new Gson();
		String a = "[{\"20200317\":{\"-M2_zYkPix-kTaesSXyX\":{\"refKey\":\"2020Mar17005648598c0314-94ee-450335-162596e8a36c\",\"time\":\"2020-03-17T09:56:48.525+09:00\",\"type\":\"WORK\"},\"-M2btzK6ginbzIY_3hr2\":{\"refKey\":\"2020Mar170951438bb2177d-905d-408d-af52-9c2995dddc32\",\"time\":\"2020-03-17T18:51:42.857+09:00\",\"type\":\"BYEBYE\"}}}]";
		final JsonElement jsonElement = gson.toJsonTree(a);
		System.out.println(jsonElement);
		final HashMap map = gson.fromJson(a.substring(1,a.length()-1), HashMap.class);
		final Object yyyymmdd = map.get(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		System.out.println(yyyymmdd);
		final Object time = map.get("time");

	}


}