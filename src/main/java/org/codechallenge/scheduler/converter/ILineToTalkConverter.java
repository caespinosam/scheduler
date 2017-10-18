package org.codechallenge.scheduler.converter;

import java.util.List;

import org.codechallenge.scheduler.model.Talk;

/**
 * Defines the methods to turn string (lines) into Talk objects
 * @author caespinosam
 *
 */
public interface ILineToTalkConverter {
	
	/**
	 * Turns string (lines) into Talk objects
	 * @param lines the string representation of each talk
	 * @return a list of Talk objects
	 */
	List<Talk> getTalks(List<String> lines);

}
