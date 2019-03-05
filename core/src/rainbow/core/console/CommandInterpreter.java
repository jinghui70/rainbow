package rainbow.core.console;


/**
 * Console输出信息对象接口
 * 
 * @author lijinghui
 *
 */
public interface CommandInterpreter {

	/**
	 *	Get the next argument in the input.
	 *	
	 *	E.g. if the commandline is hello world, the _hello method
	 *	will get "world" as the first argument.
	 */
	public String nextArgument();

	public void print(String msg, Object... args);

	/**
	 * Prints an empty line to the outputstream
	 */
	public void println();

	public void println(String msg, Object... args);

	/**
	 * Print a stack trace including nested exceptions.
	 * @param t The offending exception
	 */
	public void printStackTrace(Throwable t);

}
