package vlove.util;

import java.util.logging.ConsoleHandler;

public class StdOutConsoleHandler extends ConsoleHandler {
	/**
	 * Create a <tt>ConsoleHandler</tt> for <tt>System.err</tt>.
	 * <p>
	 * The <tt>ConsoleHandler</tt> is configured based on <tt>LogManager</tt>
	 * properties (or their default values).
	 * 
	 */
	public StdOutConsoleHandler() {
		super();
		setOutputStream(System.out);
	}
}