package com.lq.slackbot.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	List<Schedule> findAllByUsedAndChannel(Boolean used, String channel);
	List<Schedule> findAllByUsed(Boolean used);
	Schedule findByName(String name);
	List<Schedule> findAllByChannel(String channel);

}
