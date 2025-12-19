package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VanityConsole {
	
	// Pretty ANSI escape codes. Look at them, so cute <3
	private static final String COLOR_RESET = "\u001B[0m"; // Reset color
	private static final String COLOR_RED = "\u001B[31m"; // Red color
	private static final String COLOR_GREEN = "\u001B[32m"; // Green color
	private static final String COLOR_YELLOW = "\u001B[33m"; // Yellow color
	private static final String COLOR_BLUE = "\u001B[34m"; // Blue color
	private static final String COLOR_PURPLE = "\u001B[35m"; // Purple color
	private static final String COLOR_BOLD = "\u001B[1m"; // Bold text

	public static final String TERMINAL = "\u001B[36m $ \u001B[0m"; // Cyan color

    // Format for logging timestamps
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public static void shout(String message) {
		System.out.println(COLOR_YELLOW+"["+ COLOR_PURPLE + LocalDateTime.now().format(FORMATTER) + COLOR_YELLOW+"] " + COLOR_RESET + message);
	}

	public static void error(String message) {
		System.err.println(COLOR_YELLOW+"["+ COLOR_PURPLE + LocalDateTime.now().format(FORMATTER) + COLOR_YELLOW+"] " + COLOR_RED + "[E] " + message + COLOR_RESET);
	}

	public static void info(String message) {
		System.out.println(COLOR_YELLOW+"["+ COLOR_PURPLE + LocalDateTime.now().format(FORMATTER) + COLOR_YELLOW+"] " + COLOR_GREEN + "[I] " + message + COLOR_RESET);
	}
	public static void debug(String message) {
		System.out.println(COLOR_YELLOW+"["+ COLOR_PURPLE + LocalDateTime.now().format(FORMATTER) + COLOR_YELLOW+"] " + COLOR_BLUE + "[D] " + message + COLOR_RESET);
	}
	public static void warn(String message) {
		System.out.println(COLOR_YELLOW+"["+ COLOR_PURPLE + LocalDateTime.now().format(FORMATTER) + COLOR_YELLOW+"] "  + "[W] " + message + COLOR_RESET);
	}
	public static void panic(String message) {
		System.err.println(COLOR_RED+"[" + LocalDateTime.now().format(FORMATTER) + "] " + COLOR_BOLD + "[!] " +message + COLOR_RESET);
	}


	 
	public static void test() {
		shout("This is a test message, it should be white with a purple timestamp");
		error("This is a error message, it should be red");
		info("This is an info message, it should be green");
		debug("This is a debug message, it should be blue");
		warn("This is a warning message, it should be yellow");
		panic("This is a panic message; for very nasty things, it should be bold and red");
	}
}