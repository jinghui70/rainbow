package rainbow.core.console;

public abstract class HelpBuilder {

	public final static String tab = "\t";
	public final static String newline = "\r\n";

	/** helper method for getHelp. Formats the help headers. */
	public static void addHeader(String header, StringBuilder help) {
		help.append(newline);
		help.append("---");
		help.append(header);
		help.append("---");
	}

	/** helper method for getHelp. Formats the command descriptions. */
	public static void addCommand(String command, String description,
			StringBuilder help) {
		help.append(newline);
		help.append(tab);
		help.append(command);
		help.append(" - ");
		help.append(description);
	}

	/**
	 * helper method for getHelp. Formats the command descriptions with command
	 * arguements.
	 */
	public static void addCommand(String command, String parameters,
			String description, StringBuilder help) {
		help.append(newline);
		help.append(tab);
		help.append(command);
		help.append(" ");
		help.append(parameters);
		help.append(" - ");
		help.append(description);
	}

}
