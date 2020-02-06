package com.lq.slackbot.domain;

import java.util.Arrays;

public enum Restaurant {
	NO1(1, "후레쉬빌"),
	NO2(2, "록스플레이트"),
	NO3(3, "이태리부대찌개"),
	NO4(4, "독도왕새우튀김"),
	NO5(5, "전주콩나물국밥"),
	NO6(6, "놀부부대찌개"),
	NO7(7, "희래등"),
	NO8(8, "삼군김치찌개"),
	NO9(9, "교동전선생"),
	NO10(10, "크라이치즈버거"),
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
