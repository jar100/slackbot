package com.lq.slackbot.domain.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String name;

	private long count;

	private String channel;

	@Builder.Default
	private boolean isUse = true;

	public String actionValue() {
		return channel + "_" + id;
	}

	public String view() {
		return "*" +
				this.name +
				"*\n" +
				this.count +
				"\n";
	}

	public String buttonStyle() {
		if (isUse) {
			return "primary";
		}
		return "danger";
	}

	public String viewOnOff() {
		if (isUse) {
			return "ON";
		}
		return "OFF";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Restaurant that = (Restaurant) o;
		return id == that.id &&
				Objects.equals(name, that.name) &&
				Objects.equals(channel, that.channel);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, channel);
	}

	public void increaseCount() {
		count++;
	}

	public String blockId() {
		return String.format("restaurant_%s_%s", channel, id);
	}

	public String onAction() {
		return "restaurant_on";
	}

	public String offAction() {
		return "restaurant_off";
	}

	public String onOffAction() {
		if (isUse) {
			return offAction();
		}
		return onAction();
	}

	public void updateUse(String action) {
		if ("restaurant_off".equals(action)) {
			this.isUse = false;
			return;
		}
		this.isUse = true;
	}
}
