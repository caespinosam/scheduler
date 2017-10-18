package org.codechallenge.scheduler.model;

import org.codechallenge.scheduler.reader.PropertiesConfig;
import org.junit.Assert;
import org.junit.Test;

public class TrackTest {

	@Test
	public void increment_Morning_Current_Capacity_When_Talk_Added() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", 30);
		long oldCapacity = track.getMorningSession().getCurrentConsumedCapacity();
		track.addMornigTalk(talk);
		long newCapacity = track.getMorningSession().getCurrentConsumedCapacity();
		Assert.assertEquals(oldCapacity + 30, newCapacity);

	}

	@Test
	public void increment_Afternoon_Current_Capacity_When_Talk_Added() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", 30);
		long oldCapacity = track.getAfternoonSession().getCurrentConsumedCapacity();
		track.addAfternoonTalk(talk);
		long newCapacity = track.getAfternoonSession().getCurrentConsumedCapacity();
		Assert.assertEquals(oldCapacity + 30, newCapacity);

	}

	@Test
	public void morning_FilledUp_When_All_Capacity_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", PropertiesConfig.getMorningUnitsCapacity());
		Assert.assertFalse(track.isMorningSessionFilledUp());
		track.addMornigTalk(talk);
		Assert.assertTrue(track.isMorningSessionFilledUp());

	}

	@Test
	public void afternoon_Accepts_Talks_When_Not_Capacity_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", PropertiesConfig.getAfternoonMaxUnitsCapacity() - 1);
		track.addAfternoonTalk(talk);
		Assert.assertFalse(track.isAfternoonFilledUp());
		Assert.assertTrue(track.isAfternoonAcceptingTalks());

	}

	@Test
	public void afternoon_DoesNot_Accept_Talks_When_Capacity_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", PropertiesConfig.getAfternoonMaxUnitsCapacity());
		track.addAfternoonTalk(talk);
		Assert.assertTrue(track.isAfternoonFilledUp());
		Assert.assertFalse(track.isAfternoonAcceptingTalks());

	}

	@Test
	public void afternoon_In_Acceptable_State_When_Minimun_Capacity_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", PropertiesConfig.getAfternoonMinUnitsCapacity());
		track.addAfternoonTalk(talk);
		Assert.assertFalse(track.isAfternoonFilledUp());
		Assert.assertTrue(track.isAfternoonInAceptableState());

	}

	@Test
	public void afternoon_Not_In_Acceptable_State_When_Minimun_Capacity_Not_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", 0);
		track.addAfternoonTalk(talk);
		Assert.assertFalse(track.isAfternoonFilledUp());
		Assert.assertFalse(track.isAfternoonInAceptableState());

	}

	@Test
	public void afternoon_FilledUp_When_Capacity_Consumed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", PropertiesConfig.getAfternoonMaxUnitsCapacity());
		track.addAfternoonTalk(talk);
		Assert.assertTrue(track.isAfternoonFilledUp());
		Assert.assertFalse(track.isAfternoonAcceptingTalks());

	}

	@Test
	public void reduce_Morning_Current_Capacity_When_Talk_Removed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", 30);
		track.addMornigTalk(talk);
		track.removeLastMorningTalk();
		Assert.assertEquals(0, track.getMorningSession().getCurrentConsumedCapacity());

	}

	@Test
	public void reduce_Afternoon_Current_Capacity_When_Talk_Removed() {
		Track track = new Track();
		Talk talk = new Talk(1, "test", 30);
		track.addAfternoonTalk(talk);
		track.removeLastAfternoonTalk();
		Assert.assertEquals(0, track.getAfternoonSession().getCurrentConsumedCapacity());

	}

	@Test
	public void generate_Times_When_Talks_Scheduled() {
		Track track = new Track();
		Talk talk1 = new Talk(1, "test", 30);
		track.addAfternoonTalk(talk1);
		Talk talk2 = new Talk(1, "test2", 30);
		track.addMornigTalk(talk2);

		track.generateTimes();
		Assert.assertNotNull(talk1.getTime());
		Assert.assertNotNull(talk2.getTime());
		Assert.assertNotNull(track.getLunchSession().getTalks().get(0).getTime());
		Assert.assertNotNull(track.getMeetSession().getTalks().get(0).getTime());

	}

}
