package com.lq.slackbot.domain.restaurant;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
		return name + "_" + id;
	}

	public String view() {
		return "*" +
				this.name +
				"*\n" +
				this.count+
				"\n";
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
		count ++;
	}
}
