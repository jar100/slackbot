package com.lq.slackbot.worklog.service;

import com.google.gson.Gson;
import com.lq.slackbot.utils.SystemUtils;
import com.lq.slackbot.worklog.domain.WorkLogInfo;
import com.lq.slackbot.worklog.domain.WorkLogUser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class WorkLogServiceTest3 {


    WebClient webClient;

    @BeforeEach
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(SystemUtils.WORK_LOG_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    @Test
    void name() {
        //Given
        final Mono<String> stringMono = webClient.get().uri("/records/UKTREM9U0").retrieve().bodyToMono(String.class);
        //When
        System.out.println(stringMono.block());
        //Then
    }


    @Test
    void test() {
        //Given
        String request = "<!doctype html><html><head><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/><meta charSet=\"utf-8\"/><title>Welcome to the Afterparty</title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/><title data-react-helmet=\"true\">백경훈 / LQT Work Log</title><link rel=\"stylesheet\" href=\"/static/css/bundle.0b276d79.css\"/></head><body><div id=\"root\"><div class=\"app\"><header class=\"app-header navbar\"><div class=\"container\"><img src=\"/static/media/worklog.ad01f414.svg\" height=\"20\" alt=\"Work Logger\" class=\"navbar-brand-minimized clickable\"/><ul class=\"ml-auto navbar-nav\"><li class=\"px-3 nav-item\"><a href=\"/coffeebreak\" alt=\"커피투게더\" class=\"nav-link\">☕️타임</a></li><li class=\"px-3 nav-item\"></li><li class=\"dropdown nav-item\"><a aria-haspopup=\"true\" href=\"#\" class=\"nav-link\" aria-expanded=\"false\"><span class=\"circle-border\"><img src=\"/static/media/avatar.e6e4093a.svg\" class=\"img-avatar\" alt=\"\"/></span></a><div tabindex=\"-1\" role=\"menu\" aria-hidden=\"true\" class=\"dropdown-menu dropdown-menu-right\"><button type=\"button\" tabindex=\"0\" role=\"menuitem\" class=\"dropdown-item\"> Login</button></div></li></ul></div></header><div class=\"app-body\"><div class=\"container\"><div class=\"card\"><div class=\"card-body\"><div class=\"justify-content-start row\"><div class=\"col-sm-1 col\"><div class=\"avatar\"><img src=\"https://avatars.slack-edge.com/2019-11-06/810899832562_84e56b7d29a0ebec748e_72.jpg\" class=\"img-avatar\" alt=\"백경훈 / LQT\"/></div></div><div class=\"col-md-6 col\"><div><div>백경훈 / LQT</div><div class=\"small text-muted\">slack id: <!-- -->gh.baek</div></div></div></div></div></div><div class=\"card\"><div class=\"card-body\"><div class=\"DateRangePicker DateRangePicker_1 DateRangePicker__block DateRangePicker__block_2\"><div><div class=\"DateRangePickerInput DateRangePickerInput_1 DateRangePickerInput__block DateRangePickerInput__block_2\"><div class=\"DateInput DateInput_1\"><input type=\"text\" class=\"DateInput_input DateInput_input_1\" aria-label=\"Start Date\" id=\"startDate\" name=\"startDate\" value=\"12/06/2020\" placeholder=\"Start Date\" autoComplete=\"off\" aria-describedby=\"DateInput__screen-reader-message-startDate\"/><p class=\"DateInput_screenReaderMessage DateInput_screenReaderMessage_1\" id=\"DateInput__screen-reader-message-startDate\">Navigate forward to interact with the calendar and select a date. Press the question mark key to get the keyboard shortcuts for changing dates.</p></div><div class=\"DateRangePickerInput_arrow DateRangePickerInput_arrow_1\" aria-hidden=\"true\" role=\"presentation\"><svg class=\"DateRangePickerInput_arrow_svg DateRangePickerInput_arrow_svg_1\" focusable=\"false\" viewBox=\"0 0 1000 1000\"><path d=\"M694 242l249 250c12 11 12 21 1 32L694 773c-5 5-10 7-16 7s-11-2-16-7c-11-11-11-21 0-32l210-210H68c-13 0-23-10-23-23s10-23 23-23h806L662 275c-21-22 11-54 32-33z\"></path></svg></div><div class=\"DateInput DateInput_1\"><input type=\"text\" class=\"DateInput_input DateInput_input_1\" aria-label=\"End Date\" id=\"endDate\" name=\"endDate\" value=\"12/12/2020\" placeholder=\"End Date\" autoComplete=\"off\" aria-describedby=\"DateInput__screen-reader-message-endDate\"/><p class=\"DateInput_screenReaderMessage DateInput_screenReaderMessage_1\" id=\"DateInput__screen-reader-message-endDate\">Navigate backward to interact with the calendar and select a date. Press the question mark key to get the keyboard shortcuts for changing dates.</p></div></div></div></div></div></div><div class=\"card\"><div class=\"card-body\"><div class=\"chart-wrapper\"><canvas height=\"150\" width=\"300\"></canvas></div></div><div class=\"card-footer\"><div class=\"row\"><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-primary\"><div class=\"text-muted\">09:43:49</div><div>근무시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-info\"><div class=\"text-muted\">-30:16:10</div><div>초과근무시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-info\"><div class=\"text-muted\">10:43:49</div><div>일한시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout css-13bbeza\"><div class=\"text-muted\">00:00:00</div><div>재택근무시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-danger\"><div class=\"text-muted\">00:00:00</div><div>긴급대응시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-warning\"><div class=\"text-muted\">00:00:00</div><div>휴식시간</div></div></div><div class=\"mb-sm-2 mb-0 col-md\"><div class=\"callout callout-info\"><div class=\"text-muted\">01:00:00</div><div>법정 휴식시간</div></div></div></div></div></div><div class=\"css-21xqgz\"></div></div></div></div></div><script id=\"server-app-state\" type=\"application/json\">{\"userId\":\"UKTREM9U0\",\"userInfo\":{\"email\":\"gh.baek@yanolja.com\",\"id\":\"UKTREM9U0\",\"name\":\"gh.baek\",\"profile_url\":\"https:\\u002F\\u002Favatars.slack-edge.com\\u002F2019-11-06\\u002F810899832562_84e56b7d29a0ebec748e_72.jpg\",\"real_name\":\"백경훈 \\u002F LQT\",\"userUid\":\"n0fzdNHWgzd7FN14rHJgXncz7682\"},\"records\":[{\"20201207\":{\"-MNuegVazCit_UyPWfpr\":{\"refKey\":\"2020Dec070047191b2b3301-d9f2-434c-b662-f47dadacff88\",\"time\":\"2020-12-07T09:47:19.055+09:00\",\"type\":\"WORK\"},\"-MNxASJD7-i0D9G2diET\":{\"refKey\":\"2020Dec07122942a85f7844-f8dd-4821-b28d-eb71ca9e7cbd\",\"time\":\"2020-12-07T11:30:00.000Z\",\"type\":\"BYEBYE\"},\"-MNxCFIni4TTgXO1nF5F\":{\"refKey\":\"2020Dec07123733caa24726-b94c-42b0-ac92-bd5c8ba56d14\",\"time\":\"2020-12-07T21:37:33.116+09:00\",\"type\":\"WORK\"},\"-MNxCSGXhGq4YfRpjkHx\":{\"refKey\":\"2020Dec07123826bb2ae3c4-cc81-4fd3-ab4c-18b87d980881\",\"time\":\"2020-12-07T21:38:42.072+09:00\",\"type\":\"BYEBYE\"}}}],\"initialStartDate\":\"2020-12-06\",\"initialEndDate\":\"2020-12-12\",\"holidays\":[]}</script><script type=\"text/javascript\" src=\"/static/js/bundle.e5c171b9.js\" defer=\"\" crossorigin=\"anonymous\"></script></body></html>\n";
        Gson gson = new Gson();

        final Element elementById = Jsoup.parseBodyFragment(request).getElementById("server-app-state");
        final String s = elementById.childNodes().get(0).toString();
        final WorkLogInfo workLogUser = gson.fromJson(s, WorkLogInfo.class);
        System.out.println(elementById);

        //When
        //Then
    }
}