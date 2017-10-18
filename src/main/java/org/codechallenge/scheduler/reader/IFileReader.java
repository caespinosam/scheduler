package org.codechallenge.scheduler.reader;

import java.io.IOException;
import java.util.List;

/**
 * Defines the methods to read data (talks) from a datasource.
 * 
 * @author caespinosam
 *
 */
public interface IFileReader {

	/**
	 * Reads the datasource.
	 * 
	 * @param path
	 *            the path of the file to read from
	 * @return a list containing all the talks data found in the datasource
	 */
	List<String> read(String path) throws IOException;

}
