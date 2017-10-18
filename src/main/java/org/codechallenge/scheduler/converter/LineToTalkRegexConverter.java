package org.codechallenge.scheduler.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codechallenge.scheduler.model.Talk;

/**
 * Turns string into Talks objects using regex.
 * 
 * @author caespinosam
 *
 */
public class LineToTalkRegexConverter implements ILineToTalkConverter {

	/** the talk pattern to math: title xmin**/
	private static final String TITLE_PATTERN = "^(\\D+)\\s((\\d+)min|lightning)$";
	private static final String FIVE_MINUTES_NICKNAME = "lightning";
	
	private static final int GROUP_TALK_TITLE = 1;
	private static final int GROUP_TALK_MIN = 2;
	private static final int GROUP_TALK_MIN_NICKNAME = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.codechallenge.scheduler.converter.ILineToTalkConverter#getTalks(java.
	 * util.List)
	 */
	@Override
	public List<Talk> getTalks(List<String> lines) {
		List<Talk> talks = new ArrayList<Talk>();
		Pattern pattern = Pattern.compile(TITLE_PATTERN);
		int counterId = 1;
		for (String line : lines) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				String title = m.group(GROUP_TALK_TITLE);
				String minutesTmp = m.group(GROUP_TALK_MIN);
				int minutes;
				if (FIVE_MINUTES_NICKNAME.equalsIgnoreCase(minutesTmp)) {
					minutes = 5;
				} else {
					minutesTmp = m.group(GROUP_TALK_MIN_NICKNAME);
					minutes = Integer.parseInt(minutesTmp);
				}
				talks.add(new Talk(counterId, title, minutes));
				counterId++;
			}

		}

		return talks;
	}

}
