package darp.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Implements a Log to log all events happening while the program is running.
 * @author Noah Losch, Simon Rabich
 */
public class Log {

	private static Logger logger;

	/**
	 * Initializes globally usable Logger.
	 * <p>
	 * Logger can be used to log events to a file located in ./logs
	 */
	private static void initializeLogger() {
		// try configuring logger
		try {
			// check if directory for logs exists
			File logDirectory = new File("./logs/");
			if (!(logDirectory.exists())) {
				logDirectory.mkdir();
			}
			// add fileHandler with pattern for .log files
			// it creates a maximum of 10 different files
			FileHandler fileHandler = new FileHandler("logs/darp_log_%g.log", 0, 10, false);
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new SimpleFormatter());
			// turn on logging to console
			logger.setUseParentHandlers(true);
			logger.addHandler(fileHandler);
		} catch (IOException | SecurityException e) {
			throw new RuntimeException("Failed to initialize logger", e);
		}
	}

	/**
	 * Returns the Logger used in this program.
	 * @return Logger which should be used
	 */
	public static Logger getLogger() {
		if (logger == null) {
			synchronized (Log.class) {
				if (logger == null) {
					logger = Logger.getLogger("DarpLogger");
					initializeLogger();
				}
			}
		}
		return logger;
	}
}