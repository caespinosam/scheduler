package org.codechallenge.scheduler.manager;

import java.util.ArrayList;
import java.util.List;

import org.codechallenge.scheduler.converter.ILineToTalkConverter;
import org.codechallenge.scheduler.converter.LineToTalkRegexConverter;
import org.codechallenge.scheduler.model.Talk;
import org.codechallenge.scheduler.model.Track;
import org.codechallenge.scheduler.model.Workshop;
import org.codechallenge.scheduler.reader.PropertiesConfig;
import org.junit.Assert;
import org.junit.Test;

public class QueueSchedulerTest {
	
	IScheduler scheduler = new QueueScheduler();
	ILineToTalkConverter converter = new LineToTalkRegexConverter();
	
	@Test
	public void generate_Workshop_When_Talks_Are_Sent() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 65min");
		lines.add("More Java for people 40min");
		lines.add("Fun with Kotlin 30min");
		lines.add("Managing dependencies with Maven 45min");
		lines.add("Better error handling in Java 45min");
		lines.add("Scala from JEE guys lightning");
		lines.add("Slack for old people 60min");
		lines.add("Finance Domain explained 45min");
		lines.add("Healthier lunch in Berlin 30min");
		lines.add("Scope Future 30min");
		lines.add("Better work in Teams 45min");
		lines.add("Best Spring Features 60min");
		lines.add("Advance Spring Boot 60min");
		lines.add("Why Clojure Matters 45min");
		lines.add("Living in Berlin 30min");
		lines.add("Working with Azure 30min");
		lines.add("Maintain Java Code 60min");
		lines.add("Better Way of reading Books 30min");
		lines.add("What you need to know about ExtJS 30min");
		
		List<Talk> talks = converter.getTalks(lines);
		Workshop ws = scheduler.schedule(talks);
		Assert.assertEquals(2, ws.getTracks().size());
		for (Track track : ws.getTracks()) {
			Assert.assertEquals(PropertiesConfig.getMorningUnitsCapacity(), track.getMorningSession().getCurrentConsumedCapacity());
			Assert.assertTrue(track.getAfternoonSession().getCurrentConsumedCapacity() >= PropertiesConfig.getAfternoonMinUnitsCapacity()
					&& track.getAfternoonSession().getCurrentConsumedCapacity() <= PropertiesConfig.getAfternoonMaxUnitsCapacity());
		}
	}
	
	@Test
	public void generate_Workshop_When_Talks_Are_Sent_Case_2() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 65min");
		lines.add("More Java for people 40min");
		lines.add("Fun with Kotlin 30min");
		lines.add("Managing dependencies with Maven 45min");
		lines.add("Better error handling in Java 45min");
		lines.add("Scala from JEE guys lightning");
		lines.add("Slack for old people 60min");
		lines.add("Finance Domain explained 45min");
		lines.add("Healthier lunch in Berlin 30min");
		lines.add("Scope Future 30min");
		lines.add("Better work in Teams 45min");
		lines.add("Best Spring Features 60min");
		lines.add("Advance Spring Boot 60min");
		lines.add("Why Clojure Matters 45min");
		lines.add("Living in Berlin 30min");
		lines.add("Working with Azure 30min");
		lines.add("Maintain Java Code 60min");
		lines.add("Better Way of reading Books 30min");
		lines.add("What you need to know about ExtJS 30min");
		lines.add("OtherOne 180min");
		lines.add("OtherTwo 181min");
		lines.add("OtherThree 180min");
		lines.add("OtherFour 181min");
		
		List<Talk> talks = converter.getTalks(lines);
		Workshop ws = scheduler.schedule(talks);
		Assert.assertEquals(4, ws.getTracks().size());
		for (Track track : ws.getTracks()) {
			Assert.assertEquals(PropertiesConfig.getMorningUnitsCapacity(), track.getMorningSession().getCurrentConsumedCapacity());
			Assert.assertTrue(track.getAfternoonSession().getCurrentConsumedCapacity() >= PropertiesConfig.getAfternoonMinUnitsCapacity()
					&& track.getAfternoonSession().getCurrentConsumedCapacity() <= PropertiesConfig.getAfternoonMaxUnitsCapacity());
		}
	}
	
	@Test
	public void generate_Workshop_When_Talks_Are_Sent_Case_3() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 180min");
		lines.add("More Java for people 180min");
		lines.add("Fun with Kotlin 180min");
		lines.add("Managing dependencies with Maven 240min");		
		
		List<Talk> talks = converter.getTalks(lines);
		Workshop ws = scheduler.schedule(talks);
		Assert.assertEquals(2, ws.getTracks().size());
		for (Track track : ws.getTracks()) {
			Assert.assertEquals(PropertiesConfig.getMorningUnitsCapacity(), track.getMorningSession().getCurrentConsumedCapacity());
			Assert.assertTrue(track.getAfternoonSession().getCurrentConsumedCapacity() >= PropertiesConfig.getAfternoonMinUnitsCapacity()
					&& track.getAfternoonSession().getCurrentConsumedCapacity() <= PropertiesConfig.getAfternoonMaxUnitsCapacity());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case1() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 1min");		
		
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case2() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 200min");		
		
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case3() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 80min");		
		
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case4() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 240min");		
		
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case5() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 180min");		
		lines.add("Create better mocks for Spring Boot 250min");	
		lines.add("Create better mocks for Spring Boot 250min");	
		
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void generate_Error_When_Not_Enough_Elements_Case6() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 90min");		
		lines.add("Create better mocks for Spring Boot 90min");		
		lines.add("Create better mocks for Spring Boot 80min");	
				
		List<Talk> talks = converter.getTalks(lines);
		scheduler.schedule(talks);		
	}

}
