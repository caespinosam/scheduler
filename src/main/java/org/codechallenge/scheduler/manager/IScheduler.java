package org.codechallenge.scheduler.manager;

import java.util.List;

import org.codechallenge.scheduler.model.Talk;
import org.codechallenge.scheduler.model.Workshop;

/**
 * Defines the methods to schedule a list of talks.
 * @author caespinosam
 *
 */
public interface IScheduler {

	/**
	 * Schedules a list of talks in a worshop.
	 * @param talks the list to schedule
	 * @return the workshop containing the scheduled talks.
	 */
	Workshop schedule(List<Talk> talks);
}
