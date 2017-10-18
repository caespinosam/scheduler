package org.codechallenge.scheduler.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads data line by line from a text file.
 * @author caespinosam
 *
 */
public class TextFileReader implements IFileReader {

	/*
	 * (non-Javadoc)
	 * @see org.codechallenge.scheduler.IFileReader#read(java.lang.String)
	 */
	@Override
	public List<String> read(String filePath) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			return stream.map(s -> s.trim()).collect(Collectors.toList());
		}
	}

}
