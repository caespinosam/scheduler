package org.codechallenge.scheduler.manager;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codechallenge.scheduler.model.Talk;
import org.codechallenge.scheduler.model.Track;
import org.codechallenge.scheduler.model.Workshop;
import org.codechallenge.scheduler.reader.PropertiesConfig;

/**
 * Implements the algorithm to schedule a list of talks.
 * 
 * This is a greedy algorithm that puts all the data in a queue, then it pops
 * each element verifying that it complies the restrictions. If the element does
 * not comply the restrictions , the algorithm removes it as a solution for the
 * current iteration and tries with the next element.
 * 
 * In theory, this is way to test every possible combination, but the algorithm
 * ends when the first solution is found.
 * 
 * The steps are:
 * 
 * 1. Put all the elements in a queue of unscheduled elements.
     2. Create a track.
     3. For each element in the queue:
     	
       2.1 If the morning session is not filled up:
     		2.1.1 Verify that the element can be put in the session.
     			2.1.1.a If possible: put the element in the session and remove it from the queue. 
     			2.1.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.
     				2.1.1.b.1 If all the elements were already tested: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
     		2.1.2 Go to step 3. 
     				
    	2.2 If the afternoon session is not completely filled up:
     		2.2.1 Verify that the element can be put in the session. 
     			2.2.1.a If possible: put the element in the session and remove it from the queue.     				
     			2.2.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.          
           		2.2.1.b.1 If all the element were tested
           				2.2.1.b.1.a If the current state allows the Meet Event to start: create a new track.
           				2.2.1.b.1.b If the current state does not allow the Meet Event to start: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
           	2.2.2 Go to step 3.
  
  		2.3 If the afternoon session is completely filled up:
  			2.3.1 Create a new track
  			2.3.2 Go to step 3.   
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
		Deque<Set<Talk>> ruledOutTalksIterations = new LinkedList<>();
		ruledOutTalksIterations.push(new HashSet<>());

		Track currentTrack = new Track();
		workshop.addTrack(currentTrack);

		while (!unscheduledTalks.isEmpty()) {

			Set<Talk> ruledOutTalksCurrentIteration = ruledOutTalksIterations.peek();
			// gets the next element to schedule
			Talk currentTalk = unscheduledTalks.poll();
			verifyConditionsOnTalk(currentTalk);

			
			if (!currentTrack.isMorningSessionFilledUp()) {
				// starts filling up the morning session
				if (!ruledOutTalksCurrentIteration.contains(currentTalk)) {

					if (!unscheduledTalks.isEmpty()) {
						// try to schedule the current element
						boolean inserted = currentTrack.addMornigTalk(currentTalk);
						verifyInsertedElement(inserted, currentTalk, ruledOutTalksCurrentIteration,
								ruledOutTalksIterations, unscheduledTalks);
					} else {
						// not enough elements to fill this session, step back
						// to find a new solution.
						verifyInsertedElement(false, currentTalk, ruledOutTalksCurrentIteration,
								ruledOutTalksIterations, unscheduledTalks);
					}
				} else {
					// All the elements were tested with no success, so a new
					// solution is tested by removing the last scheduled element
					// (revert the last insertion)
					Talk removedTalk = currentTrack.removeLastMorningTalk();
					unscheduledTalks.add(currentTalk);
					if (removedTalk == null) {
						// the session did not have more elements, so the
						// current track
						// needs to be removed as well
						workshop.removeLastTrack();
						currentTrack = workshop.getLastTrack();
						if (currentTrack == null) {
							throw new IllegalArgumentException("All elements were tested. No solution. ");
						}
						// the removed element is the last talk of the previous
						// track
						removedTalk = currentTrack.removeLastAfternoonTalk();
					}
					ruledOutTalksCurrentIteration = removeElementFromPreviousIteration(ruledOutTalksIterations,
							removedTalk, unscheduledTalks);
				}
			} else if (currentTrack.isAfternoonAcceptingTalks()) {
				// starts filling up the afternoon session
				if (!ruledOutTalksCurrentIteration.contains(currentTalk)) {

					long currentAfternoonCapacity = currentTrack.getAfternoonSession().getCurrentConsumedCapacity();
					boolean sessionNotFilledUp = currentTalk.getLength()
							+  currentAfternoonCapacity < PropertiesConfig.getAfternoonMinUnitsCapacity();
					if (unscheduledTalks.isEmpty() && sessionNotFilledUp) {
						// dismiss the current solution (iteration)
						verifyInsertedElement(false, currentTalk, ruledOutTalksCurrentIteration,
								ruledOutTalksIterations, unscheduledTalks);

					} else {
						// try to schedule the current element
						boolean inserted = currentTrack.addAfternoonTalk(currentTalk);
						verifyInsertedElement(inserted, currentTalk, ruledOutTalksCurrentIteration,
								ruledOutTalksIterations, unscheduledTalks);

						if (inserted && currentTrack.isAfternoonFilledUp() && !unscheduledTalks.isEmpty()) {
							// the session is completely filled up after inserting
							// the element, a new track is created because there are still unscheduled elements
							currentTrack = createNewTrack(null, workshop.getTracks(), ruledOutTalksIterations,
									unscheduledTalks);
						}
					}

				} else {

					if (currentTrack.isAfternoonInAceptableState()) {
						// a new track is created, but the current element was not inserted
						currentTrack = createNewTrack(currentTalk, workshop.getTracks(), ruledOutTalksIterations,
								unscheduledTalks);

					} else {
						// all the elements were tested and no solution was found, step back
						Talk removedTalk = currentTrack.removeLastAfternoonTalk();
						if (removedTalk == null) {
							removedTalk = currentTrack.removeLastMorningTalk();
						}
						if (!unscheduledTalks.contains(currentTalk)) {
							unscheduledTalks.add(currentTalk);
						}

						ruledOutTalksCurrentIteration = removeElementFromPreviousIteration(ruledOutTalksIterations,
								removedTalk, unscheduledTalks);
					}

				}
			}

		}

		for (Track tr : workshop.getTracks()) {
			tr.generateTimes();
		}

		return workshop;
	}

	/**
	 * Gets the ruled out elements of the previous iteration and removes the given element.
	 * @param ruledOutTalksIterations contains all the ruled put elements for all the iterations
	 * @param removedTalk the element to remove
	 * @param unscheduledTalks elements that have not been scheduled. 
	 * @return
	 */
	private Set<Talk> removeElementFromPreviousIteration(Deque<Set<Talk>> ruledOutTalksIterations, Talk removedTalk,
			Deque<Talk> unscheduledTalks) {
		ruledOutTalksIterations.poll();
		Set<Talk> ruledOutTalks = ruledOutTalksIterations.peek();
		ruleOutTalk(removedTalk, ruledOutTalks, unscheduledTalks);
		return ruledOutTalks;
	}

	/**
	 * Dismiss an element as a solution for the current iteration.
	 * 
	 * @param talk
	 *            the element to dismiss
	 * @param ruledOutTalks
	 *            set of already dismissed elements for the current iteration
	 *   
	 * @param unscheduledTalks
	 *            the available elements to schedule.
	 */
	private void ruleOutTalk(Talk talk, Set<Talk> ruledOutTalks, Deque<Talk> unscheduledTalks) {
		if (talk != null) {
			if (!unscheduledTalks.contains(talk)) {
				unscheduledTalks.add(talk);
			}
			ruledOutTalks.add(talk);
		}
	}

	/**
	 * Verifies if an element was inserted as a solution for the current
	 * iteration. If not, the element is dismissed and sent back to the queue of
	 * possible solutions for future iterations.
	 * 
	 * @param inserted
	 *            if the element was inserted or not.
	 * @param talk
	 *            the element.
	 * @param ruledOutTalks
	 *            set of already dismissed elements for the current iteration.
	 * @param ruledOutTalksIterations contains all the ruled out elements for all the iterations
	 * @param unscheduledTalks
	 *            the available elements to schedule.
	 */
	private void verifyInsertedElement(boolean inserted, Talk talk, Set<Talk> ruledOutTalks,
			Deque<Set<Talk>> ruledOutTalksIterations, Deque<Talk> unscheduledTalks) {
		if (inserted) {
			ruledOutTalksIterations.push(new HashSet<>());
		} else {
			// the current element doesnt fit in the session. It is
			// put back into the available elements
			// and is ignored in the current iteration
			ruleOutTalk(talk, ruledOutTalks, unscheduledTalks);
		}
	}

	/**
	 * Creates a new track because the previous one is already filled up. Puts
	 * back the current talk to the queue so that it can be reused in the next
	 * iteration.
	 * 
	 * @param tracks
	 *            list of current tracks.
	 * @param ruledOutTalks
	 *            set of already dismissed elements.
	 *            
	 * @param ruledOutTalksIterations contains all the ruled out elements for all the iterations
	 *            
	 * @param currentTalk the talk to insert in the new track, null if no talk will be inserted.
	 * @return the new track.
	 */
	private Track createNewTrack(Talk currentTalk, List<Track> tracks, Deque<Set<Talk>> ruledOutTalksIterations,
			Deque<Talk> unscheduledTalks) {
		Track track = new Track();
		tracks.add(track);

		if (currentTalk != null && !unscheduledTalks.contains(currentTalk)) {
			unscheduledTalks.add(currentTalk);
		}

		ruledOutTalksIterations.peek().clear();

		return track;
	}

	/**
	 * Verifies that a talk complies the conditions.
	 * 
	 * @param talk
	 */
	private void verifyConditionsOnTalk(Talk talk) {

		if (talk.getLength() > PropertiesConfig.getMorningUnitsCapacity()
				&& talk.getLength() > PropertiesConfig.getAfternoonMaxUnitsCapacity()) {
			throw new IllegalArgumentException(
					"It is not possible to find a solution. Talk " + talk + " exceeds the limits.");
		}
	}
}
