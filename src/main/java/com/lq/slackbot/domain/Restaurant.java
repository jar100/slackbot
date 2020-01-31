package com.lq.slackbot.domain;

import java.util.Arrays;

public enum Restaurant {
	NO1(1, "후레쉬빌"),
	NO2(2, "록스플레이트"),
	NO3(3, "독도새우"),
	NONE(0,"없음");

	;

	private int index;
	private String name;

	Restaurant(final int index, final String name) {
		this.index = index;
		this.name = name;
	}

	public static Restaurant of(int index) {
		return Arrays.stream(values()).filter(o->o.index == index).findFirst().orElse(NONE);
	}

	public String getName() {
		return name;
	}
}
