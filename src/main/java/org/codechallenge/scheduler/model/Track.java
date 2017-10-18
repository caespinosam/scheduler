package org.codechallenge.scheduler.model;

import java.time.LocalTime;

import org.codechallenge.scheduler.reader.PropertiesConfig;

public class Track {

	private Session morningSession = new Session();
	private Session afternoonSession = new Session();
	private Session lunchSession = new Session();
	private Session meetSession = new Session();

	public Session getMorningSession() {
		return morningSession;
	}

	public Session getAfternoonSession() {
		return afternoonSession;
	}

	public Session getLunchSession() {
		return lunchSession;
	}

	public Session getMeetSession() {
		return meetSession;
	}

	/**
	 * Puts a talk in the morning session.
	 * @param t the talk to put
	 * @return true if the talk was put, false when it was not possible to add the talk
	 */
	public boolean addMornigTalk(Talk t) {
		if (t.getLength() + morningSession.getCurrentConsumedCapacity() <= PropertiesConfig.getMorningUnitsCapacity()) {
			morningSession.addTalk(t);
			return true;
		}

		return false;
	}

	/**
	 * Puts a talk in the afternoon session.
	 * @param t the talk to put
	 * @return true if the talk was put, false when it was not possible to add the talk
	 */
	public boolean addAfternoonTalk(Talk t) {
		if (t.getLength() + afternoonSession.getCurrentConsumedCapacity() <= PropertiesConfig
				.getAfternoonMaxUnitsCapacity()) {
			afternoonSession.addTalk(t);
			return true;
		}

		return false;
	}

	/**
	 * Verifies if the morning session has room for more talks.
	 * @return true no room, false otherwise
	 */
	public boolean isMorningSessionFilledUp() {

		return morningSession.getCurrentConsumedCapacity() == PropertiesConfig.getMorningUnitsCapacity();
	}

	/**
	 * Verifies if it is possible to continue adding talks in the afternoon session.
	 * @return
	 */
	public boolean isAfternoonAcceptingTalks() {

		return afternoonSession.getCurrentConsumedCapacity() < PropertiesConfig.getAfternoonMaxUnitsCapacity();
	}

	/**
	 * Verifies if the afternoon session contains enough talks to start the meet event (e.g between 4pm - 5pm).
	 * @return
	 */
	public boolean isAfternoonInAceptableState() {

		return afternoonSession.getCurrentConsumedCapacity() >= PropertiesConfig.getAfternoonMinUnitsCapacity()
				&& afternoonSession.getCurrentConsumedCapacity() <= PropertiesConfig.getAfternoonMaxUnitsCapacity();
	}

	/**
	 * Verifies if the afternoon session has reached its maximum capacity.
	 * @return
	 */
	public boolean isAfternoonFilledUp() {

		return afternoonSession.getCurrentConsumedCapacity() == PropertiesConfig.getAfternoonMaxUnitsCapacity();
	}

	/**
	 * Removes a talk from the morning session.
	 * @return the talk that was removed, null if none was removed.
	 */
	public Talk removeLastMorningTalk() {
		if (!morningSession.getTalks().isEmpty()) {
			Talk lastTalk = morningSession.getTalks().get(morningSession.getTalks().size() - 1);
			morningSession.removeTalk(lastTalk);
			return lastTalk;
		}
		return null;
	}

	/**
	 * Removes a talk from the afternoon session.
	 * @return the talk that was removed, null if none was removed.
	 */
	public Talk removeLastAfternoonTalk() {
		if (!afternoonSession.getTalks().isEmpty()) {
			Talk lastTalk = afternoonSession.getTalks().get(afternoonSession.getTalks().size() - 1);
			afternoonSession.removeTalk(lastTalk);
			return lastTalk;
		}
		return null;
	}

	/**
	 * Calculates the time of each talk depending on their length.
	 */
	public void generateTimes() {

		LocalTime currentTime = PropertiesConfig.getMorningSessionBegin();
		for (Talk talk : morningSession.getTalks()) {
			talk.setTime(currentTime);
			currentTime = currentTime.plusMinutes(talk.getLength());
		}

		Talk lunch = new Talk(PropertiesConfig.LUNCH_TITLE);
		lunch.setTime(PropertiesConfig.getLunchBegin());
		lunchSession.addTalk(lunch);

		currentTime = PropertiesConfig.getAfternoonSessionBegin();
		for (Talk talk : afternoonSession.getTalks()) {
			talk.setTime(currentTime);
			currentTime = currentTime.plusMinutes(talk.getLength());
		}

		Talk meetEvent = new Talk(PropertiesConfig.MEET_EVENT_TITLE);
		meetEvent.setTime(currentTime);
		meetSession.addTalk(meetEvent);
	}

}
