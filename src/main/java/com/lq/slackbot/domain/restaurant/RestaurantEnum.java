package com.lq.slackbot.domain.restaurant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


//일단 디폴트로 만들어놓고 추가하는 형식으로 할까??
public enum RestaurantEnum {
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
	NO11(11, "포포빈"),
	NO12(12, "카레마치"),
	NO13(13, "소공동"),
	NO14(14, "얌샘김밥");

	private int index;
	private String name;

	RestaurantEnum(final int index, final String name) {
		this.index = index;
		this.name = name;
	}

//	public static Restaurant of(int index) {
//		return Arrays.stream(values()).filter(o->o.index == index).findFirst().orElse(NONE);
//	}

	public static List<RestaurantEnum> list() {
		final List<RestaurantEnum> collect = Arrays.stream(values()).collect(Collectors.toList());
		Collections.shuffle(collect);
		return collect;
	}

	public Restaurant ToRestaurant(String channel) {
		return Restaurant.builder().name(this.name).channel(channel).count(0).build();
	}

	public String getName() {
		return name;
	}
}