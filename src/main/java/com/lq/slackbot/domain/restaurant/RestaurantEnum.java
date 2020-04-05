package com.lq.slackbot.domain.restaurant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


//일단 디폴트로 만들어놓고 추가하는 형식으로 할까??
public enum RestaurantEnum {
	NO1(1, "록스플레이트"),
	NO2(2, "메콩타이"),
	NO3(3, "희래등"),
	NO4(4, "교동전선생"),
	NO5(5, "길농원"),
	NO6(6, "남산옥"),
	NO7(7, "남원추어탕풍천장어구이"),
	NO8(8, "놀부부대찌개"),
	NO9(9, "동해횟집"),
	NO10(10, "명동칼국수(치킨뱅뱅)"),
	NO11(11, "백구식당"),
	NO12(12, "보스골뱅이(오베로)"),
	NO13(13, "본도시락(봉은사역점)"),
	NO14(14, "삼군김치찌개(삼성점)"),
	NO15(15, "삼성골"),
	NO16(16, "소공동뚝배기"),
	NO17(17, "송담추어탕"),
	NO18(18, "예자네밥상"),
	NO19(19, "우리집"),
	NO20(20, "이태리부대찌개"),
	NO21(21, "임고집한우"),
	NO22(22, "전주콩나물국밥"),
	NO23(23, "족발보쌈마을"),
	NO24(24, "카레마치"),
	NO25(25, "후레쉬빌"),
	NO26(26, "요리왕"),
	NO27(27, "치찌중화식탁"),
	NO28(28, "사조참치"),
	NO29(29, "독도왕새우튀김"),
	NO30(30, "명동할머니국수"),
	NO31(31, "얌샘김밥"),
	NO32(32, "포포빈"),
	NO33(33, "레스토랑G"),
	NO34(34, "크라이치즈버거"),
	NO35(35, "원더볼")
	;

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
