package rainbow.core.console;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

import rainbow.core.bundle.BundleListener;
import rainbow.core.extension.Extension;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.Platform;

/**
 * 命令解析器，用于解析从console发过来的用户指令，并发给注册的 <code>CommandProvider</code>
 * 
 * FrameworkCommandInterpreter还提供对"more"命令的支持，用户可以设置显示的行数。
 * 
 * @author lijinghui
 * 
 */
public class CommandInterpreterImpl implements CommandInterpreter, BundleListener {

    /** The command line in StringTokenizer form */
    private StringTokenizer tok;

    /** The FrameworkConsole */
    private Console con;

    /** The stream to send output to */
    private PrintWriter out;

    /**
     * The maximum number of lines to print without user prompt. 0 means no user
     * prompt is required, the window is scrollable.
     */
    protected static int maxLineCount;

    /** The number of lines printed without user prompt. */
    protected int currentLineCount;

    /** 当前的commandProvider扩展 */
    private Extension curProviderExtension;

    /**
     * The constructor. It turns the cmdline string into a StringTokenizer and
     * remembers the input parms.
     */
    public CommandInterpreterImpl(Console con) {
        this.con = con;
        this.out = con.getWriter();
        ExtensionRegistry.registerExtension(null, BundleListener.class, this);
    }

    public String getPrompt() {
        CommandProvider provider = getCurProvider();
        return provider == null ? "ucp" : provider.getName();
    }

    private CommandProvider getCurProvider() {
        return curProviderExtension == null ? null : (CommandProvider) curProviderExtension.getObject();
    }

    /**
     * Execute a command line as if it came from the end user.
     * 
     * Searches the list of command providers using introspection until it finds
     * one that contains a matching method. It searches for a method with the
     * name "_cmd" where cmd is the command to execute. For example, for a
     * command of "launch" execute searches for a method called "_launch".
     * 
     * @param cmd
     *            The name of the command to execute.
     */
    public void execute(String cmd) {
        tok = new StringTokenizer(cmd);
        resetLineCount();

        // handle top command here
        cmd = nextArgument();
        CommandProvider provider = getCurProvider();
        if (checkSystemCommand(cmd, provider))
            return;
        if (!executeCommand(cmd, provider)) {
            print("没有找到命令 ");
            println(cmd);
        }
    }

    private boolean checkSystemCommand(String cmd, CommandProvider provider) {
        if ("help".equals(cmd) || "?".equals(cmd)) {
            out.println(getHelp(provider));
        } else if ("exit".equals(cmd)) {
            println();
            Platform.shutdown();
            System.exit(0); // NOPMD
        } else if ("gc".equals(cmd)) {
            _gc();
        } else if ("more".equals(cmd)) {
            _more();
        } else if (selectCommandProvider(cmd)) {
            return true;
        } else
            return false;
        return true;
    }

    private String getHelp(CommandProvider provider) {
        StringBuilder sb = new StringBuilder();
        HelpBuilder.addHeader("系统命令", sb);
        HelpBuilder.addCommand("exit", "shutdown and exit", sb);
        HelpBuilder.addCommand("gc", "perform a garbage collection", sb);
        HelpBuilder.addCommand("threads", "display threads and thread groups", sb);
        HelpBuilder.addCommand("more", "More prompt for console output", sb);
        HelpBuilder.addHeader("命令组", sb);
        for (CommandProvider cp : ExtensionRegistry.getExtensionObjects(CommandProvider.class)) {
            HelpBuilder.addCommand(cp.getName(), cp.getDescription(), sb);
        }
        if (provider != null) {
            HelpBuilder.addHeader(provider.getDescription(), sb);
            provider.getHelp(sb);
        }
        return sb.toString();
    }

    private void _gc() { // NOPMD
        long before = Runtime.getRuntime().freeMemory();
        /* Let the finilizer finish its work and remove objects from its queue */
        System.gc(); /* asyncronous garbage collector might already run */
        System.gc(); /* to make sure it does a full gc call it twice */
        System.runFinalization();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // do nothing
        }
        long after = Runtime.getRuntime().freeMemory();
        print("Total memory:");
        println(String.valueOf(Runtime.getRuntime().totalMemory()));
        print("Free memory before GC:");
        println(String.valueOf(before));
        print("Free memory after GC:");
        println(String.valueOf(after));
        print("Memory gained with GC:");
        println(String.valueOf(after - before));
    }

    /**
     * Toggles the use of the more prompt for displayed output.
     * 
     */
    public void _more() { // NOPMD
        if (confirm("Use 'more' prompt?", true)) {
            int lines = prompt("enter maximum number of lines to scroll:", 24);
            setMaximumLinesToScroll(lines);
        } else {
            setMaximumLinesToScroll(0);
        }
    }

    private boolean selectCommandProvider(String cmd) {
        for (Extension extension : ExtensionRegistry.getExtensions(CommandProvider.class)) {
            CommandProvider cp = (CommandProvider) extension.getObject();
            if (cmd.equalsIgnoreCase(cp.getName())) {
                curProviderExtension = extension;
                return true;
            }
        }
        return false;
    }

    /**
     * @param cmd
     * @param provider
     * @return
     */
    private boolean executeCommand(String cmd, CommandProvider provider) {
        boolean executed = false;
        if (provider != null)
            try {
                Method method = provider.getClass().getMethod("_" + cmd, CommandInterpreter.class);
                method.invoke(provider, this);
                executed = true; // stop after the command has been found
            } catch (NoSuchMethodException ite) {
                // keep going - wrong command
            } catch (InvocationTargetException ite) {
                executed = true; // don't want to keep trying - we found the
                                 // method
                // but got an error
                printStackTrace(ite.getTargetException());
            } catch (Exception ee) {
                executed = true; // don't want to keep trying - we got an error
                                 // we
                // don't understand
                printStackTrace(ee);
            }
        return executed;
    }

    /**
     * Answers the number of lines output to the console window should scroll
     * without user interaction.
     * 
     * @return The number of lines to scroll.
     */
    private int getMaximumLinesToScroll() {
        return maxLineCount;
    }

    /**
     * Sets the number of lines output to the console window will scroll without
     * user interaction.
     * <p>
     * Note that this number does not include the line for the 'more' prompt
     * itself.
     * <p>
     * If the number of lines is 0 then no 'more' prompt is disabled.
     * 
     * @param lines
     *            the number of lines to scroll
     */
    private void setMaximumLinesToScroll(int lines) {
        if (lines < 0) {
            throw new IllegalArgumentException("CONSOLE_LINES_TO_SCROLL_NEGATIVE_ERROR");
        }
        maxLineCount = lines;
    }

    /**
     * Resets the line counter for the 'more' prompt.
     */
    private void resetLineCount() {
        currentLineCount = 0;
    }

    @Override
    public String nextArgument() {
        if (tok == null || !tok.hasMoreElements())
            return null;
        return tok.nextToken();
    }

    @Override
    public void print(String msg, Object... args) {
        synchronized (out) {
            check4More();
            if (args.length == 0)
                out.print(msg);
            else
                out.print(String.format(msg, args));
            out.flush();
        }
    }

    /**
     * Prints a empty line to the outputstream
     */
    public void println() {
        println("");
    }

    /**
     * Print a stack trace including nested exceptions.
     * 
     * @param t
     *            The offending exception
     */
    public void printStackTrace(Throwable t) {
        t.printStackTrace(out);

        Method[] methods = t.getClass().getMethods();

        int size = methods.length;
        Class<Throwable> throwable = Throwable.class;

        for (int i = 0; i < size; i++) {
            Method method = methods[i];

            if (Modifier.isPublic(method.getModifiers())
                    && method.getName().startsWith("get") && throwable.isAssignableFrom(method.getReturnType()) && (method.getParameterTypes().length == 0)) { //$NON-NLS-1$
                try {
                    Throwable nested = (Throwable) method.invoke(t);

                    if ((nested != null) && (nested != t)) {
                        out.println("CONSOLE_NESTED_EXCEPTION");
                        printStackTrace(nested);
                    }
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    @Override
    public void println(String msg, Object... args) {
        synchronized (out) {
            check4More();
            print(msg, args);
            out.println();
            currentLineCount++;
        }
    }

    /**
     * Displays the more... prompt if the max line count has been reached and
     * waits for the operator to hit enter.
     * 
     */
    private void check4More() {
        int max = getMaximumLinesToScroll();
        if (max > 0) {
            if (currentLineCount >= max) {
                out.print("-- More...Press Enter to Continue...");
                out.flush();
                con.getInput(); // wait for user entry
                resetLineCount(); // Reset the line counter for the 'more'
            }
        }
    }

    /**
     * Prompts the user for confirmation.
     * 
     * @param string
     *            the message to present to the user to confirm
     * @param defaultAnswer
     *            the default result
     * 
     * @return <code>true</code> if the user confirms; <code>false</code>
     *         otherwise.
     */
    protected boolean confirm(String string, boolean defaultAnswer) {
        synchronized (out) {
            if (string.length() > 0) {
                print(string);
            } else {
                print("Confirm?");
            }
            print(" (y/n default="); //$NON-NLS-1$
            if (defaultAnswer) {
                print("y) "); //$NON-NLS-1$
            } else {
                print("n) "); //$NON-NLS-1$
            }
        }
        String input = con.getInput();
        resetLineCount();
        if (input.length() == 0) {
            return defaultAnswer;
        }
        return input.toLowerCase().charAt(0) == 'y';
    }

    /**
     * Prompts the user for input from the input medium providing a default
     * value.
     * 
     * @param string
     *            the message to present to the user
     * @param defaultAnswer
     *            the string to use as a default return value
     * 
     * @return The user provided string or the defaultAnswer, if user provided
     *         string was empty.
     */
    protected String prompt(String string, String defaultAnswer) {
        println();
        if (string.length() > 0) {
            if (defaultAnswer.length() > 0) {
                StringBuilder sb = new StringBuilder(256);
                sb.append(string);
                sb.append(" (default=");
                sb.append(defaultAnswer);
                sb.append(") "); //$NON-NLS-1$
                print(sb.toString());
            } else {
                print(string);
            }
        }
        String input = con.getInput();
        resetLineCount();
        if (input.length() > 0) {
            return input;
        }
        return defaultAnswer;
    }

    /**
     * Prompts the user for input of a positive integer.
     * 
     * @param string
     *            the message to present to the user
     * @param defaultAnswer
     *            the integer to use as a default return value
     * 
     * @return The user provided integer or the defaultAnswer, if user provided
     *         an empty input.
     */
    protected int prompt(String string, int defaultAnswer) {
        Integer i = Integer.valueOf(defaultAnswer);
        int answer;
        while (true) {
            String s = prompt(string, i.toString());
            try {
                answer = Integer.parseInt(s);
                if (answer >= 0) {
                    return answer;
                }
            } catch (NumberFormatException e) {
            }
            println("invalid input");
        }
    }

    public void bundleStop(String id) {
        if (curProviderExtension == null)
            return;
        if (curProviderExtension.getBundle().equals(id))
            curProviderExtension = null;
    }

    @Override
    public void bundleStarted(String id) {
    }

    @Override
    public void bundleStopping(String id) {
    }

}
