package rainbow.core.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.google.common.base.Throwables;

/**
 * This class starts frame with a console for development use.
 * 
 * @author lijinghui
 * 
 */
public class Console implements Runnable {
    /** The stream to receive commands on */
    protected BufferedReader in;

    /** The stream to write command results to */
    protected PrintWriter out;

    /** The command interpreter */
    protected final CommandInterpreterImpl cmdInterpreter;

    public Console() {
        getDefaultStreams();
        cmdInterpreter = new CommandInterpreterImpl(this);
    }

    /**
     * Open streams for system.in and system.out
     */
    private void getDefaultStreams() {
        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    PrintWriter getWriter() {
        return out;
    }

    /**
     * Begin doing the active part of the class' code. Starts up the console.
     */
    public void run() {
        while (true) {
            out.print("\r\n");
            out.print(cmdInterpreter.getPrompt());
            out.print(">");
            out.flush();

            String cmdline = null;
            try {
                cmdline = in.readLine();
            } catch (IOException ioe) {
                out.print(Throwables.getStackTraceAsString(ioe));
            }
            if (cmdline == null) {
                break;
            }
            doCommand(cmdline);
        }
    }

    private void doCommand(String cmdline) {
        cmdline = cmdline.trim();
        if (cmdline.length() > 0) {
            cmdInterpreter.execute(cmdline);
        }
    }

    public String getInput() {
        String input;
        try {
            input = in.readLine();
        } catch (IOException e) {
            input = "";
        }
        return input;
    }

}
