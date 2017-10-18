package org.codechallenge.scheduler.model;

import java.util.ArrayList;
import java.util.List;

public class Session {

	private long currentConsumedCapacity ;
	private List<Talk> talks = new ArrayList<>();

	public void addTalk(Talk t) {
		talks.add(t);
		currentConsumedCapacity = currentConsumedCapacity + t.getLength();
	}

	public void removeTalk(Talk t) {
		talks.remove(t);
		currentConsumedCapacity = currentConsumedCapacity - t.getLength();
	}

	public List<Talk> getTalks() {
		return talks;
	}

	public long getCurrentConsumedCapacity() {
		return currentConsumedCapacity;
	}

}
