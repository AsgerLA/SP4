package Class;
import java.io.OutputStream;
import java.io.PrintStream;

public class Log {
    private static PrintStream out = System.out;


    public static void setOutputStream(OutputStream outStream) {
        if (outStream != null) {
            out = new PrintStream(outStream);
        } else {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
    }


    public static void warn(String msg) {
        out.println("[WARN] " + msg);
    }


    public static void error(String msg) {
        out.println("[ERROR] " + msg);
    }


    public static void flush() {
        out.flush();
    }
}

