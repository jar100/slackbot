package com.lq.slackbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {


	@GetMapping("/1")
	public String ipcheck(HttpServletRequest request) {
		log.info("x-forwerd : {}" ,request.getHeader("X-FORWARDED-FOR"));
		log.info("reomteAddr : {}" ,request.getRemoteAddr());

		return (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
	}
}
