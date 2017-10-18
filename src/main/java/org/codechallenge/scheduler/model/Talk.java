package org.codechallenge.scheduler.model;

import java.time.LocalTime;

import org.codechallenge.scheduler.reader.PropertiesConfig;

public class Talk {

	private int id; // artifical ID
	private String title;
	private long length;
	private LocalTime time;

	public Talk(String title) {
		super();
		this.title = title;
	}

	public Talk(int id, String title, long length) {
		super();
		this.id = id;
		this.title = title;
		this.length = length;
	}

	public String getTitle() {
		return title;
	}

	public long getLength() {
		return length;
	}

	public String getFormattedLength() {
		if (PropertiesConfig.LUNCH_TITLE.equals(title)) {
			return "";
		}
		else if (PropertiesConfig.MEET_EVENT_TITLE.equals(title)) {
			return "";
		}		
		else if (length == 5) {
			return "lightning";
		}
		return length + "min";
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Talk other = (Talk) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (time == null ? "" : time.format(PropertiesConfig.getTimeFormatter())) + " " + title + " " + getFormattedLength();
	}

}
