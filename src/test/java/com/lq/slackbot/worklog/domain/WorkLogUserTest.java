package com.lq.slackbot.worklog.domain;

import com.ibm.icu.util.ChineseCalendar;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class WorkLogUserTest {

	@Test
	void localDateTimeTest() {
		String test = "출근! 09:20";
		final String[] split = test.split("!");
		final String[] split1 = split[1].split(":");
		System.out.println(split1[0].trim());
		final int i = Integer.parseInt(split1[0].trim());
		System.out.println(i);
		System.out.println(split1[1]);


		final LocalDateTime localDateTime = LocalDateTime.now().withHour(i).withMinute(Integer.parseInt(split1[1]));
		System.out.println(localDateTime);
	}

	@Test
	void localDateTimeException() {
		String test = "출근! 09:2012313";
		final String[] split = test.split("!");
		final String[] split1 = split[1].split(":");
		System.out.println(split1[0].trim());
		final int i = Integer.parseInt(split1[0].trim());
		System.out.println(i);
		System.out.println(split1[1]);


		final LocalDateTime localDateTime = LocalDateTime.now().withHour(i).withMinute(Integer.parseInt(split1[1]));
		System.out.println(localDateTime);
	}

	@Test
	void name() {
		final String s = convertLunarToSolar("20200706");
		System.out.println(s);
	}

	/**
	 * 음력날짜를 양력날짜로 변환
	 * @param yyyymmdd (음력날짜)
	 * @return 양력날짜 (yyyyMMdd)
	 */
	private static String convertLunarToSolar(String yyyymmdd) {
		ChineseCalendar cc = new ChineseCalendar();
		Calendar cal = Calendar.getInstance();

		if (yyyymmdd == null)
			return "";

		String date = yyyymmdd.trim();
		if (date.length() != 8) {
			if (date.length() == 4)
				date = date + "0101";
			else if (date.length() == 6)
				date = date + "01";
			else if (date.length() > 8)
				date = date.substring(0, 8);
			else
				return "";
		}

		cc.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
		cc.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		cc.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

		cal.setTimeInMillis(cc.getTimeInMillis());

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);

		StringBuffer ret = new StringBuffer();
		ret.append(String.format("%04d", y));
		ret.append(String.format("%02d", m));
		ret.append(String.format("%02d", d));

		return ret.toString();
	}
}