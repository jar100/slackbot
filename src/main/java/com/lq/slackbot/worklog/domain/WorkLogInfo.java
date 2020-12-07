package com.lq.slackbot.worklog.domain;

import lombok.Data;

/**
 * @author gh.baek
 */
@Data
public class WorkLogInfo {
    private String userId;
    private WorkLogUser userInfo;
}
