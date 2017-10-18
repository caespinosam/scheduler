package org.codechallenge.scheduler.manager;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codechallenge.scheduler.model.Talk;
import org.codechallenge.scheduler.model.Track;
import org.codechallenge.scheduler.model.Workshop;

/**
 * Implements the algorithm to schedule a list of talks.
 * 
 * This is a greedy algorithm that puts all the data in a queue, then it pops
 * each element verifying that it complies the restrictions. If the element
 * does not comply the restrictions , the algorithm removes it as a solution
 * for the current iteration and tries with the next element. 
 * 
 * In theory, this is way to test every possible combination, but the algorithm
 * ends when the first solution is found.
 * 
 *  The steps are:
 *    
 *    1. Put all the elements in a queue of unscheduled elements.
 *    2. Create a track.
 *    3. For each element in the queue:
 *    	
 *      2.1 If the morning session is not filled up:
 *    		2.1.1 Verify that the element can be put in the session.
 *    			2.1.1.a If possible: put the element in the session and remove it from the queue. 
 *    			2.1.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.
 *    				2.1.1.b.1 If all the elements were already tested: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
 *    		2.1.2 Go to step 3. 
 *    				
 *   	2.2 If the afternoon session is not completely filled up:
 *    		2.2.1 Verify that the element can be put in the session. 
 *    			2.2.1.a If possible: put the element in the session and remove it from the queue.     				
 *    			2.2.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.          
 *          		2.2.1.b.1 If all the element were tested
 *          				2.2.1.b.1.a If the current state allows the Meet Event to start: create a new track.
 *          				2.2.1.b.1.b If the current state does not allow the Meet Event to start: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
 *          	2.2.2 Go to step 3.
 * 
 * 		2.3 If the afternoon session is completely filled up:
 * 			2.3.1 Create a new track
 * 			2.3.2 Go to step 3.  * 
 * 
 * @author caespinosam
 *
 */
public class QueueScheduler implements IScheduler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.codechallenge.scheduler.scheduler.IScheduler#schedule(java.util.List)
	 */
	@Override
	public Workshop schedule(List<Talk> talks) {

		Workshop workshop = new Workshop();	
		Deque<Talk> unscheduledTalks = new LinkedList<>(talks);
		Set<Talk> ruledOutTalks = new HashSet<>();

		
		Track currentTrack = new Track();
		workshop.addTrack(currentTrack);

		while (!unscheduledTalks.isEmpty()) {

			// gets the next element to schedule
			Talk currentTalk = unscheduledTalks.poll();

			// starts filling up the morning session
			if (!currentTrack.isMorningSessionFilledUp()) {
				if (!ruledOutTalks.contains(currentTalk)) {

					// try to schedule the current element
					boolean inserted = currentTrack.addMornigTalk(currentTalk);
					verifyInsertedElement(inserted, currentTalk, ruledOutTalks, unscheduledTalks);
				} else {
					// All the elements were tested with no success, so a new
					// solution is tested by removing the last scheduled element
					Talk removedTalk = currentTrack.removeLastMorningTalk();
					unscheduledTalks.add(currentTalk);
					ruledOutTalks.clear();
					if (removedTalk == null) {
						workshop.removeLastTrack();
						currentTrack = workshop.getLastTrack();
						if (currentTrack == null) {
							throw new IllegalArgumentException("All elements were tested. No solution. ");
						}
					} else {
						ruleOutTalk(removedTalk, ruledOutTalks, unscheduledTalks);
					}
				}
			} else if (currentTrack.isAfternoonAcceptingTalks()) {
				if (!ruledOutTalks.contains(currentTalk)) {
					// try to schedule the current element
					boolean inserted = currentTrack.addAfternoonTalk(currentTalk);
					verifyInsertedElement(inserted, currentTalk, ruledOutTalks, unscheduledTalks);
				} else {

					if (currentTrack.isAfternoonInAceptableState()) {
						currentTrack = createNewTrack(workshop.getTracks(), ruledOutTalks);
						ruleOutTalk(currentTalk, ruledOutTalks, unscheduledTalks);
					} else {
						Talk removedTalk = currentTrack.removeLastAfternoonTalk();
						unscheduledTalks.add(currentTalk);
						ruledOutTalks.clear();
						ruleOutTalk(removedTalk, ruledOutTalks, unscheduledTalks);
					}

				}
			} else if (currentTrack.isAfternoonFilledUp()) {
				// the session is completely filled up, a new track is created
				currentTrack = createNewTrack(workshop.getTracks(), ruledOutTalks);
				ruleOutTalk(currentTalk, ruledOutTalks, unscheduledTalks);
			}

		}
		
		for (Track tr : workshop.getTracks()) {
			tr.generateTimes();
		}

		return workshop;
	}

	/**
	 * Dismiss an element as a solution for the current iteration.
	 * @param talk the element to dismiss
	 * @param ruledOutTalks  set of already dismissed elements
	 * @param unscheduledTalks the available elements to schedule.
	 */
	private void ruleOutTalk(Talk talk, Set<Talk> ruledOutTalks, Deque<Talk> unscheduledTalks) {
		if (talk != null) {
			unscheduledTalks.add(talk);
			ruledOutTalks.add(talk);
		}
	}

	/**
	 * Verifies if an element was inserted as a solution for the current iteration.  If not,
	 * the element is dismissed and sent back to the queue of possible solutions for future iterations.
	 * @param inserted if the element was inserted or not.
	 * @param talk the element.
	 * @param ruledOutTalks  set of already dismissed elements.
	 * @param unscheduledTalks  the available elements to schedule.
	 */
	private void verifyInsertedElement(boolean inserted, Talk talk, Set<Talk> ruledOutTalks,
			Deque<Talk> unscheduledTalks) {
		if (inserted) {
			ruledOutTalks.clear();
		} else {
			// the current element doesnt fit in the session. It is
			// put back into the available elements
			// and is ignored in the current iteration
			ruleOutTalk(talk, ruledOutTalks, unscheduledTalks);
		}
	}

	/**
	 * Creates a new track.
	 * 
	 * @param tracks list of current tracks.
	 * @param ruledOutTalks set of already dismissed elements.
	 * @return the new track.
	 */
	private Track createNewTrack(List<Track> tracks, Set<Talk> ruledOutTalks) {
		Track track = new Track();
		tracks.add(track);
		ruledOutTalks.clear();
		return track;
	}
}
