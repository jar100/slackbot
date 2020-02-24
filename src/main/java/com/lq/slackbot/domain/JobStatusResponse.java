package com.lq.slackbot.domain;

import java.util.List;

public class JobStatusResponse {
	private int numOfAllJobs;
	private int numOfRunningJobs;
	private int numOfGroups;
	private List<JobResponse> jobs;

	public void setNumOfAllJobs(final int numOfAllJobs) {
		this.numOfAllJobs = numOfAllJobs;
	}

	public int getNumOfAllJobs() {
		return numOfAllJobs;
	}

	public void setNumOfRunningJobs(final int numOfRunningJobs) {
		this.numOfRunningJobs = numOfRunningJobs;
	}

	public int getNumOfRunningJobs() {
		return numOfRunningJobs;
	}

	public void setNumOfGroups(final int numOfGroups) {
		this.numOfGroups = numOfGroups;
	}

	public int getNumOfGroups() {
		return numOfGroups;
	}

	public void setJobs(final List<JobResponse> jobs) {
		this.jobs = jobs;
	}

	public List<JobResponse> getJobs() {
		return jobs;
	}
}
