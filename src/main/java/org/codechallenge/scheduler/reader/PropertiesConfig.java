package org.codechallenge.scheduler.reader;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.codechallenge.scheduler.Application;

/**
 * Contains some constants used by the application. 
 * @author caespinosam
 *
 */
public class PropertiesConfig {

	private static LocalTime morningSessionBegin;
	private static LocalTime lunchBegin;
	private static LocalTime afternoonSessionBegin;
	private static LocalTime meetEventBeginLowerLimit;
	private static LocalTime meetEventBeginUpperLimit;
		
	private static long morningUnitsCapacity;
	private static long afternoonMinUnitsCapacity;
	private static long afternoonMaxUnitsCapacity;
	
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
	
	public static final String LUNCH_TITLE = "Lunch";
	public static final String MEET_EVENT_TITLE = "Meet Your Colleagues Event";

	static {
		Properties p = new Properties();
		try (InputStream is = Application.class.getClassLoader().getResourceAsStream("config.properties")) {
			p.load(is);
			Integer property = Integer.parseInt(p.getProperty("session.morning.begin"));
			morningSessionBegin = LocalTime.of(property, 0);
			property = Integer.parseInt(p.getProperty("lunch.begin"));
			lunchBegin = LocalTime.of(property, 0);
			property = Integer.parseInt(p.getProperty("session.afternoon.begin"));
			afternoonSessionBegin = LocalTime.of(property, 0);
			property = Integer.parseInt(p.getProperty("meet.event.begin.lowerlimit"));
			meetEventBeginLowerLimit = LocalTime.of(property, 0);
			property = Integer.parseInt(p.getProperty("meet.event.begin.upperlimit"));
			meetEventBeginUpperLimit = LocalTime.of(property, 0);

			morningUnitsCapacity = ChronoUnit.MINUTES.between(morningSessionBegin, lunchBegin);
			afternoonMinUnitsCapacity = ChronoUnit.MINUTES.between(afternoonSessionBegin, meetEventBeginLowerLimit);
			afternoonMaxUnitsCapacity = ChronoUnit.MINUTES.between(afternoonSessionBegin, meetEventBeginUpperLimit);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static LocalTime getMorningSessionBegin() {
		return morningSessionBegin;
	}

	public static LocalTime getLunchBegin() {
		return lunchBegin;
	}

	public static LocalTime getAfternoonSessionBegin() {
		return afternoonSessionBegin;
	}

	public static LocalTime getMeetEventBeginLowerLimit() {
		return meetEventBeginLowerLimit;
	}

	public static LocalTime getMeetEventBeginUpperLimit() {
		return meetEventBeginUpperLimit;
	}

	public static long getMorningUnitsCapacity() {
		return morningUnitsCapacity;
	}

	public static long getAfternoonMinUnitsCapacity() {
		return afternoonMinUnitsCapacity;
	}

	public static long getAfternoonMaxUnitsCapacity() {
		return afternoonMaxUnitsCapacity;
	}
	
	public static DateTimeFormatter getTimeFormatter() {
		return timeFormatter;
	}

}
