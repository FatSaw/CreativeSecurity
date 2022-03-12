package gasha.creativesecurity.consolefilter;

import gasha.creativesecurity.CreativeSecurityPlugin;
import gasha.creativesecurity.consolefilter.FilterConfig;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class ConsoleFilter
implements Filter {
    private CreativeSecurityPlugin pl;
    public static FilterConfig fc;
    static List<String> filters;

    public ConsoleFilter(CreativeSecurityPlugin plugin) {
        this.pl = plugin;
        fc = new FilterConfig(this.pl, "console-filter");
    }

    public static void setup() {
        try {
            filters.clear();
            List<String> test = fc.getStrList("hide-messages");
            for (String t : test) {
                filters.add(t);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Filter.Result checkMessage(String message) {
        if (message == null) {
            return Filter.Result.NEUTRAL;
        }
        if (!filters.isEmpty()) {
            for (String s : filters) {
                if (s == null || !message.contains(s)) continue;
                return Filter.Result.DENY;
            }
        }
        return Filter.Result.NEUTRAL;
    }

    public LifeCycle.State getState() {
        try {
            return LifeCycle.State.STARTED;
        }
        catch (Exception exception) {
            return null;
        }
    }

    public void initialize() {
    }

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {
    }

    public void stop() {
    }

    public Filter.Result filter(LogEvent event) {
        return this.checkMessage(event.getMessage().getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object ... arg4) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return this.checkMessage(message.toString());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return this.checkMessage(message.getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return this.checkMessage(message);
    }

    public Filter.Result getOnMatch() {
        return Filter.Result.NEUTRAL;
    }

    public Filter.Result getOnMismatch() {
        return Filter.Result.NEUTRAL;
    }

    static {
        filters = new ArrayList<String>();
    }
}

