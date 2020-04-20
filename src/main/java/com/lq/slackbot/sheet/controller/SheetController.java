package com.lq.slackbot.sheet.controller;

import com.lq.slackbot.sheet.service.SheetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class SheetController {
	private SheetService sheetService;

	public SheetController(final SheetService sheetService) {
		this.sheetService = sheetService;
	}


	@GetMapping("/sheet/list")
	public List getList() throws IOException {
		return sheetService.getSheet();
	}
}
