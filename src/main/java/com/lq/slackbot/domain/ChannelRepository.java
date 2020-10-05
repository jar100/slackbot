package com.lq.slackbot.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channel,String> {

	@Query("select NEW com.lq.slackbot.domain.Actions(i.erpPartnerCode,count(erp_shop_code),count(case when i.activated = true then 1 )) from ErpPartner as i where i.erpPartnerCode = ?1 group by i.erpPartnerCode\"")
	Channel findBy();
}
