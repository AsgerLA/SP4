package Class;
import java.io.OutputStream;
import java.io.PrintStream;

public class Log {
    private static PrintStream out = System.out;
    private static int logLevel = 0;
    public static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_WARN  = 1;
    public static final int LOG_LEVEL_ERROR = 2;


    public static void setOutputStream(OutputStream outStream) {
        if (outStream != null) {
            out = new PrintStream(outStream);
        } else {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
    }

    public static void setLogLevel(int logLevel) {
        Log.logLevel = logLevel;
    }

    public static void debug(String msg) {
        if (logLevel <= LOG_LEVEL_DEBUG)
            out.println("[DEBUG] " + msg);
    }

    public static void warn(String msg) {
        if (logLevel <= LOG_LEVEL_WARN)
            out.println("[WARN] " + msg);
    }

    public static void error(String msg) {
        if (logLevel <= LOG_LEVEL_ERROR)
            out.println("[ERROR] " + msg);
    }

    public static void flush() {
        out.flush();
    }
}

