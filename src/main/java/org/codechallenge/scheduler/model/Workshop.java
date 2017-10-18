package org.codechallenge.scheduler.model;

import java.util.ArrayList;
import java.util.List;

public class Workshop {

	private List<Track> tracks = new ArrayList<>();

	public void addTrack(Track t) {
		tracks.add(t);
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void removeLastTrack() {
		tracks.remove(tracks.size() - 1);
	}

	public Track getLastTrack() {
		if (!tracks.isEmpty()) {
			return tracks.get(0);
		}
		return null;
	}

	public void printTalks() {
		int trackCounter = 1;
		for (Track track : getTracks()) {
			System.out.println();
			System.out.println("Track " + trackCounter);
			printTalks(track.getMorningSession());
			printTalks(track.getLunchSession());
			printTalks(track.getAfternoonSession());
			printTalks(track.getMeetSession());
			trackCounter++;
		}
	}

	private void printTalks(Session session) {
		for (Talk talk : session.getTalks()) {
			System.out.println(talk);
		}

	}
}
