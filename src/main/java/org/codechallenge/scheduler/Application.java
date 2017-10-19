
package org.codechallenge.scheduler;

import java.io.IOException;
import java.util.List;

import org.codechallenge.scheduler.converter.ILineToTalkConverter;
import org.codechallenge.scheduler.converter.LineToTalkRegexConverter;
import org.codechallenge.scheduler.manager.IScheduler;
import org.codechallenge.scheduler.manager.QueueScheduler;
import org.codechallenge.scheduler.model.Talk;
import org.codechallenge.scheduler.model.Workshop;
import org.codechallenge.scheduler.reader.IFileReader;
import org.codechallenge.scheduler.reader.TextFileReader;

/**
 * @author caespinosam
 *
 */
public class Application {

	private IFileReader fileReader = new TextFileReader();
	private ILineToTalkConverter talkConverter = new LineToTalkRegexConverter();
	private IScheduler scheduler = new QueueScheduler();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length > 0) {
			String filePath = args[0];
			new Application().schedule(filePath);
		} else {
			System.out.println("Error: No file path sent!");
		}

	}

	public void schedule(String filePath) throws IOException {
		List<String> lines = fileReader.read(filePath);
		List<Talk> talks = talkConverter.getTalks(lines);

		System.out.println("INPUT:");
		for (Talk talk : talks) {
			System.out.println(talk.getTitle() + " " + talk.getLength());
		}

		try {
			Workshop result = scheduler.schedule(talks);
			result.printTalks();

		} catch (IllegalArgumentException iae) {
			System.out.println("Illegal argument: " + iae.getMessage());
		}

	}

}
