package org.codechallenge.scheduler.converter;

import java.util.ArrayList;
import java.util.List;

import org.codechallenge.scheduler.model.Talk;
import org.junit.Assert;
import org.junit.Test;



public class LineToTalkRegexConverterTest {
	
	ILineToTalkConverter converter = new LineToTalkRegexConverter();
	
	@Test
	public void get_All_Talks_When_All_Valid_Lines() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 65min");
		lines.add("More Java for people 40min");
		lines.add("Scala from JEE guys lightning");
		
		List<Talk> talks = converter.getTalks(lines);
		Assert.assertEquals(3, talks.size());
		Assert.assertEquals("Create better mocks for Spring Boot", talks.get(0).getTitle());
		Assert.assertEquals(65, talks.get(0).getLength());
		Assert.assertEquals("Scala from JEE guys", talks.get(2).getTitle());
		Assert.assertEquals(5, talks.get(2).getLength());
	}
	
	@Test
	public void get_Filtered_Talks_When_Some_Invalid_Lines() {
		List<String> lines = new ArrayList<>();
		lines.add("Create better mocks for Spring Boot 5 65min");
		lines.add("More Java for people 40minutes");
		lines.add("Scala from JEE guys");
		lines.add("Healthier lunch in Berlin 30min");
		
		List<Talk> talks = converter.getTalks(lines);
		Assert.assertEquals(1, talks.size());
		Assert.assertEquals("Healthier lunch in Berlin", talks.get(0).getTitle());
		Assert.assertEquals(30, talks.get(0).getLength());
		
	}

}
